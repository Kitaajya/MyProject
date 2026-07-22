package org.designer.model;

import java.util.ArrayList;
import java.util.List;

public class MediaResource {

    private String title;
    private String description;
    private String baseUrl;
    private List<String> videos = new ArrayList<>();
    private List<String> audios = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<String> iframes = new ArrayList<>();

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public List<String> getVideos() { return videos; }
    public void setVideos(List<String> videos) { this.videos = videos; }

    public List<String> getAudios() { return audios; }
    public void setAudios(List<String> audios) { this.audios = audios; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public List<String> getIframes() { return iframes; }
    public void setIframes(List<String> iframes) { this.iframes = iframes; }
}
