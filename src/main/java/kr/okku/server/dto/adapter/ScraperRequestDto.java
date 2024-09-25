package kr.okku.server.dto.adapter;

public class ScraperRequestDto{
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
