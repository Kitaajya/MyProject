package org.designer.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.designer.model.MediaResource;
import org.designer.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@RestController
@RequestMapping("/api")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping("/crawl")
    public MediaResource crawl(@RequestParam String url) {
        if (url == null || url.trim().isEmpty()) {
            MediaResource error = new MediaResource();
            error.setTitle("错误");
            error.setDescription("请输入有效的网址");
            return error;
        }
        String targetUrl = url.trim();
        if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
            targetUrl = "https://" + targetUrl;
        }
        return crawlerService.crawl(targetUrl);
    }

    @GetMapping("/proxy")
    public void proxy(@RequestParam String url,
                      @RequestHeader(value = "Range", required = false) String range,
                      HttpServletResponse response) {
        try {
            URL target = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) target.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
            conn.setRequestProperty("Referer", url);
            conn.setRequestProperty("Origin", target.getProtocol() + "://" + target.getHost());
            if (range != null) {
                conn.setRequestProperty("Range", range);
            }
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);

            int code = conn.getResponseCode();

            if (code == 301 || code == 302) {
                String location = conn.getHeaderField("Location");
                conn.disconnect();
                if (location != null) {
                    proxy(location, range, response);
                }
                return;
            }

            String contentType = conn.getContentType();
            long contentLength = conn.getContentLengthLong();
            String contentRange = conn.getHeaderField("Content-Range");

            response.setStatus(code);
            if (contentType != null) {
                response.setContentType(contentType);
            }
            if (contentLength > 0) {
                response.setContentLengthLong(contentLength);
            }
            if (contentRange != null) {
                response.setHeader("Content-Range", contentRange);
            }
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Content-Disposition", "inline");

            // m3u8 需要重写内部 URL
            boolean isM3u8 = contentType != null && contentType.contains("mpegurl");
            if (!isM3u8 && contentType == null) {
                isM3u8 = url.toLowerCase().endsWith(".m3u8");
            }

            if (isM3u8) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                     OutputStream out = response.getOutputStream()) {

                    String line;
                    StringBuilder m3u8Content = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        m3u8Content.append(line).append("\n");
                    }

                    String rewritten = rewriteM3u8(m3u8Content.toString(), url);
                    byte[] data = rewritten.getBytes(StandardCharsets.UTF_8);
                    response.setContentLengthLong(data.length);
                    out.write(data);
                    out.flush();
                }
            } else {
                try (InputStream in = conn.getInputStream();
                     OutputStream out = response.getOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            response.setStatus(500);
            try {
                response.getWriter().write("代理请求失败: " + e.getMessage());
            } catch (Exception ignored) {}
        }
    }

    private String rewriteM3u8(String content, String baseUrl) throws Exception {
        URL base = new URL(baseUrl);
        String baseOrigin = base.getProtocol() + "://" + base.getHost()
                + (base.getPort() != -1 ? ":" + base.getPort() : "");

        StringBuilder result = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            String trimmed = line.trim();
            // # 开头的是标签行，不处理
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                result.append(line).append("\n");
            } else {
                // 这是 URL 行（.ts 分片、子 m3u8 等）
                String absoluteUrl;
                if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                    absoluteUrl = trimmed;
                } else if (trimmed.startsWith("/")) {
                    absoluteUrl = baseOrigin + trimmed;
                } else {
                    // 相对路径：基于 baseUrl 的父目录
                    String basePath = base.getPath();
                    String parent = basePath.substring(0, basePath.lastIndexOf('/') + 1);
                    absoluteUrl = baseOrigin + parent + trimmed;
                }

                String proxyUrl = "/api/proxy?url=" + URLEncoder.encode(absoluteUrl, "UTF-8");
                result.append(proxyUrl).append("\n");
            }
        }
        return result.toString();
    }
}
