package kr.okku.server.dto.controller.review;

public class ScrapeRequest {
    private String url;
    private String okkuId;

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOkkuId() {
        return okkuId;
    }

    public void setOkkuId(String okkuId) {
        this.okkuId = okkuId;
    }
}
