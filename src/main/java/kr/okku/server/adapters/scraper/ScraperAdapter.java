package kr.okku.server.adapters.scraper;

import io.sentry.Sentry;
import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.dto.adapter.FittingResponseDto;
import kr.okku.server.dto.adapter.ScraperRequestDto;
import kr.okku.server.dto.adapter.ScraperResponseDto;
import kr.okku.server.dto.controller.review.ScrapeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ScraperAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ScraperAdapter.class);

    private final RestTemplate restTemplate;
    @Value("${spring.msa.scraper.uri}")
    private String scraperUrl;
    private final String checkrUrl = scraperUrl + "/status";

    public ScraperAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ScrapedDataDomain> scrape(String url) {
        try {
            ScraperResponseDto response = restTemplate.postForObject(scraperUrl + "/scrap", new ScraperRequestDto(url), ScraperResponseDto.class);

            // Logging the successful request
            System.out.printf("Scraping successful for URL: %s\n", url);

            return Optional.ofNullable(ScrapedDataDomain.builder()
                    .price(response.getPrice())
                    .image(response.getImg_url())
                    .name(response.getName())
                    .platform(response.getPlatform())
                    .productPk(response.getProduct_key())
                    .url(response.getUrl())
                    .build());
        } catch (Exception e) {
            System.err.printf("Failed to scrape data for URL: %s. Error: %s\n", url, e.getMessage());
            Sentry.withScope(scope -> {
                scope.setExtra("url", url);
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return Optional.ofNullable(null);
        }
    }

    public boolean fitting(String userId, String clothesClass, byte[] itemImage, byte[] userImage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("user_pk", userId);
            body.add("clothes_class", clothesClass);
            body.add("human_img", userImage);
            body.add("clothes_img", itemImage);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

            ResponseEntity<FittingResponseDto> response = restTemplate.exchange(
                    scraperUrl + "/fitting",
                    HttpMethod.POST,
                    requestEntity,
                    FittingResponseDto.class
            );

            System.out.printf("Fitting successful with response time: %.2f ms\n", response.getBody().getResponseTime());

            return true;
        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return false;
        }
    }
}
