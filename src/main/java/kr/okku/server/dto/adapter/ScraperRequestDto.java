package kr.okku.server.dto.adapter;

import lombok.Data;

@Data
public class ScraperRequestDto {
    private String path;

    public ScraperRequestDto(String path) {
        this.path = path;
    }
}
