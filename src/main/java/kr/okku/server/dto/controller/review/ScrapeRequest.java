package kr.okku.server.dto.controller.review;

import kr.okku.server.dto.controller.BasicRequestDto;

public class ScrapeRequest extends BasicRequestDto {
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
