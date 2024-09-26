package kr.okku.server.dto.adapter;

public class FittingRequestDto {
    private String path;

    public FittingRequestDto(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
