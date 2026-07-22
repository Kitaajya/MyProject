package org.designer.service;

import org.designer.model.MediaResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    // 匹配标准 URL 格式的视频链接
    private static final Pattern VIDEO_URL_PATTERN = Pattern.compile(
            "https?://[^\"'\\s<>]+\\.(mp4|m3u8|flv|webm|mkv|avi|mov|ts|mpd)(\\?[^\"'\\s<>]*)?",
            Pattern.CASE_INSENSITIVE
    );

    // 匹配 JS 中 "url":"..." 或 "url":"..." 格式的视频链接（含转义斜杠）
    private static final Pattern VIDEO_JSON_PATTERN = Pattern.compile(
            "\"url\"\\s*:\\s*\"(https?:\\\\?/[^\"]+\\.(mp4|m3u8|flv|webm|mkv|avi|mov|ts|mpd)[^\"]*)\"",
            Pattern.CASE_INSENSITIVE
    );

    // 匹配 JS 中 player_xxx 对象里所有 URL 字段
    private static final Pattern PLAYER_URL_PATTERN = Pattern.compile(
            "\"(?:url|src|link|file|path|stream)\"\\s*:\\s*\"(https?:\\\\?/[^\"]+)\"",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern AUDIO_URL_PATTERN = Pattern.compile(
            "https?://[^\"'\\s<>]+\\.(mp3|wav|ogg|aac|flac|m4a|wma)(\\?[^\"'\\s<>]*)?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IMG_URL_PATTERN = Pattern.compile(
            "https?://[^\"'\\s<>]+\\.(jpg|jpeg|png|gif|bmp|webp|svg|ico)(\\?[^\"'\\s<>]*)?",
            Pattern.CASE_INSENSITIVE
    );

    public MediaResource crawl(String targetUrl) {
        MediaResource resource = new MediaResource();
        resource.setBaseUrl(targetUrl);

        try {
            Document doc = Jsoup.connect(targetUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            // 标题
            Element titleEl = doc.selectFirst("title");
            resource.setTitle(titleEl != null ? titleEl.text() : "无标题");

            // 描述
            Element descEl = doc.selectFirst("meta[name=description]");
            if (descEl == null) descEl = doc.selectFirst("meta[property=og:description]");
            resource.setDescription(descEl != null ? descEl.attr("content") : "无描述");

            Set<String> videos = new LinkedHashSet<>();
            Set<String> audios = new LinkedHashSet<>();
            Set<String> images = new LinkedHashSet<>();
            Set<String> iframes = new LinkedHashSet<>();

            // ===== 1. HTML 标签提取 =====

            // <video>
            for (Element el : doc.select("video")) {
                addAbsUrl(videos, el.attr("src"), targetUrl);
                addAbsUrl(videos, el.attr("poster"), targetUrl);
                for (Element s : el.select("source")) {
                    addAbsUrl(videos, s.attr("src"), targetUrl);
                }
            }

            // <audio>
            for (Element el : doc.select("audio")) {
                addAbsUrl(audios, el.attr("src"), targetUrl);
                for (Element s : el.select("source")) {
                    addAbsUrl(audios, s.attr("src"), targetUrl);
                }
            }

            // <iframe>
            for (Element el : doc.select("iframe")) {
                addAbsUrl(iframes, el.attr("src"), targetUrl);
                addAbsUrl(iframes, el.attr("data-src"), targetUrl);
            }

            // <img> (含懒加载属性)
            for (Element el : doc.select("img")) {
                addAbsUrl(images, el.attr("src"), targetUrl);
                addAbsUrl(images, el.attr("data-src"), targetUrl);
                addAbsUrl(images, el.attr("data-original"), targetUrl);
                addAbsUrl(images, el.attr("data-lazy-src"), targetUrl);
                addAbsUrl(images, el.attr("data-echo"), targetUrl);
                addAbsUrl(images, el.attr("data-url"), targetUrl);
                addAbsUrl(images, el.attr("data-video"), targetUrl);
            }

            // Open Graph / Twitter
            for (Element el : doc.select("meta[property=og:video], meta[property=og:video:url], meta[property=og:video:secure_url], meta[name=twitter:player:stream]")) {
                addAbsUrl(videos, el.attr("content"), targetUrl);
            }
            for (Element el : doc.select("meta[property=og:image], meta[property=og:image:url]")) {
                addAbsUrl(images, el.attr("content"), targetUrl);
            }

            // JSON-LD
            for (Element el : doc.select("script[type=application/ld+json]")) {
                String json = el.html();
                extractUrlsFromJson(json, videos, "embedUrl");
                extractUrlsFromJson(json, videos, "contentUrl");
                extractUrlsFromJson(json, videos, "url");
                extractUrlsFromJson(json, images, "thumbnailUrl");
                extractUrlsFromJson(json, images, "image");
            }

            // ===== 2. 全页面源码正则扫描（捕获 JS 动态加载的链接） =====
            String pageHtml = doc.html();

            // 扫描 script 标签内容
            for (Element el : doc.select("script")) {
                String scriptContent = el.html();
                if (!scriptContent.isEmpty()) {
                    // 标准 URL 匹配
                    extractByRegex(scriptContent, VIDEO_URL_PATTERN, videos);
                    // JSON 格式 URL 匹配（如 player_aaaa 中的 "url":"..."）
                    extractByRegex(scriptContent, VIDEO_JSON_PATTERN, videos);
                    // player 对象中的 url/src/link/file/path/stream 字段
                    extractByRegex(scriptContent, PLAYER_URL_PATTERN, videos);
                    extractByRegex(scriptContent, AUDIO_URL_PATTERN, audios);
                    extractByRegex(scriptContent, IMG_URL_PATTERN, images);
                }
            }

            // 扫描 style 标签中的背景视频/图片
            for (Element el : doc.select("style")) {
                String css = el.html();
                extractByRegex(css, VIDEO_URL_PATTERN, videos);
                extractByRegex(css, IMG_URL_PATTERN, images);
            }

            // 扫描行内 style 属性
            for (Element el : doc.select("[style]")) {
                String style = el.attr("style");
                extractByRegex(style, VIDEO_URL_PATTERN, videos);
                extractByRegex(style, IMG_URL_PATTERN, images);
            }

            // 扫描 a 标签的 href（可能是视频下载链接）
            for (Element el : doc.select("a[href]")) {
                String href = el.attr("href");
                if (matchesExt(href, ".mp4", ".m3u8", ".flv", ".webm", ".mkv")) {
                    addAbsUrl(videos, href, targetUrl);
                } else if (matchesExt(href, ".mp3", ".wav", ".ogg", ".aac")) {
                    addAbsUrl(audios, href, targetUrl);
                }
            }

            // 扫描 data-* 属性中的视频链接
            for (Element el : doc.select("[data-src], [data-video], [data-url], [data-background]")) {
                for (Attribute attr : el.attributes()) {
                    String val = attr.getValue();
                    if (matchesExt(val, ".mp4", ".m3u8", ".flv", ".webm")) {
                        addAbsUrl(videos, val, targetUrl);
                    } else if (matchesExt(val, ".mp3", ".wav", ".ogg")) {
                        addAbsUrl(audios, val, targetUrl);
                    }
                }
            }

            // 整个页面源码再扫一遍（兜底）
            extractByRegex(pageHtml, VIDEO_URL_PATTERN, videos);
            extractByRegex(pageHtml, AUDIO_URL_PATTERN, audios);

            resource.setVideos(new ArrayList<>(videos));
            resource.setAudios(new ArrayList<>(audios));
            resource.setImages(new ArrayList<>(images));
            resource.setIframes(new ArrayList<>(iframes));

            log.info("爬取完成: {} | 视频:{} 音频:{} 图片:{} 嵌入:{}",
                    targetUrl, videos.size(), audios.size(), images.size(), iframes.size());

        } catch (Exception e) {
            log.error("爬取失败: {} - {}", targetUrl, e.getMessage());
            resource.setTitle("爬取失败");
            resource.setDescription("错误信息: " + e.getMessage());
        }

        return resource;
    }

    private void addAbsUrl(Set<String> set, String raw, String baseUrl) {
        if (raw == null || raw.isEmpty() || raw.startsWith("data:")) return;
        try {
            URL abs = new URL(new URL(baseUrl), raw);
            set.add(abs.toString());
        } catch (Exception e) {
            set.add(raw);
        }
    }

    private void extractByRegex(String text, Pattern pattern, Set<String> set) {
        if (text == null || text.isEmpty()) return;
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            String url;
            // 如果有捕获组，优先取第一个捕获组（URL 本身）
            if (m.groupCount() >= 1 && m.group(1) != null) {
                url = m.group(1);
            } else {
                url = m.group();
            }
            // 清理转义斜杠和尾部引号
            url = url.replace("\\/", "/");
            url = url.replaceAll("[\"')\\]>]+$", "");
            set.add(url);
        }
    }

    private boolean matchesExt(String url, String... exts) {
        if (url == null || url.isEmpty()) return false;
        String lower = url.toLowerCase();
        for (String ext : exts) {
            if (lower.contains(ext)) return true;
        }
        return false;
    }

    private void extractUrlsFromJson(String json, Set<String> set, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        while (idx != -1) {
            int start = json.indexOf("\"", idx + search.length()) + 1;
            int end = json.indexOf("\"", start);
            if (start > 0 && end > start) {
                String val = json.substring(start, end);
                if (val.startsWith("http")) {
                    set.add(val);
                }
            }
            idx = json.indexOf(search, end);
        }
    }
}
