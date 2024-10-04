package kr.okku.server.dto.adapter;

import java.io.Serializable;

public class ScraperRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String path;

    public ScraperRequestDto(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
