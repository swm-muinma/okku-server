package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
@Data
@Builder
public class ScrapedDataDomain {
    private String name;
    private int price;
    private String image;
    private String url;
    private String productPk;
    private String platform;
    private String brand;
    private String category;
    private String fittingPart;

    // ScrapedDataDomain을 설정하는 메서드
    public static ScrapedDataDomain fromDocument(Document document, String url) {
        String title = getMetaTagContent(document, "meta[property=og:title]");
        String image = getMetaTagContent(document, "meta[property=og:image]");
        String platform = extractDomain(url);

        return ScrapedDataDomain.builder()
                .name(Optional.ofNullable(title).orElse("제목 없음"))
                .image(Optional.ofNullable(image).orElse("default-image.png"))
                .url(url)
                .platform(Optional.ofNullable(platform).orElse("Unknown Platform"))
                .build();
    }

    // 도메인 추출 로직을 내부 메서드로 이동
    private static String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain != null ? domain.replace("www.", "") : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    // 메타 태그에서 콘텐츠 추출
    private static String getMetaTagContent(Document document, String cssQuery) {
        Element element = document.selectFirst(cssQuery);
        return element != null ? element.attr("content") : null;
    }
}