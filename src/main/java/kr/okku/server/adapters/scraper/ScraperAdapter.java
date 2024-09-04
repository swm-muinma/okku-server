package kr.okku.server.adapters.scraper;

import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.dto.adapter.ScraperResponseDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ScraperAdapter {

    private final RestTemplate restTemplate;
    @Value("${spring.msa.scraper.uri}")
    private String scraperUrl;
    private final String checkrUrl = scraperUrl + "/status";

    public ScraperAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ScrapedDataDomain> scrape(String url) {
        try {
            ScraperResponseDto response = restTemplate.postForObject(scraperUrl + "/scrap", new ScrapeRequest(url), ScraperResponseDto.class);
            return Optional.ofNullable(ScrapedDataDomain.builder()
                    .price(response.getPrice())
                    .image(response.getImg_url())
                    .name(response.getName())
                    .platform(response.getPlatform())
                    .productPk(response.getProduct_key())
                    .url(response.getUrl())
                    .build());
        } catch (Exception e) {
            return Optional.ofNullable(null);
        }
    }


    // Inner class for request body
    static class ScrapeRequest {
        private String path;

        public ScrapeRequest(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
