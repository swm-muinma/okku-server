package kr.okku.server.dto.adapter.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageUrlMessageContent extends MessageContent {
    @JsonProperty("image_url") // JSON 직렬화 시 image_url로 출력
    private ImageUrl imageUrl; // 필드명을 imageUrl로 변경

    public ImageUrlMessageContent(String url) {
        this.imageUrl = new ImageUrl(url); // 생성자에서 imageUrl 사용
    }

    public ImageUrl getImageUrl() {
        return imageUrl;
    }

    public static class ImageUrl {
        @JsonProperty("url") // JSON 직렬화 시 url로 출력
        private String url;

        public ImageUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
