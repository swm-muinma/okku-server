package kr.okku.server.adapters.scraper;

import io.sentry.Sentry;
import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.dto.adapter.FittingRequestDto;
import kr.okku.server.dto.adapter.FittingResponseDto;
import kr.okku.server.dto.adapter.ScraperRequestDto;
import kr.okku.server.dto.adapter.ScraperResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ScraperAdapter {

    private final ScraperClientAdapter scraperClientAdapter;

    @Autowired
    public ScraperAdapter(ScraperClientAdapter scraperClientAdapter) {
        this.scraperClientAdapter = scraperClientAdapter;
    }
    public Optional<ScrapedDataDomain> scrape(String url) {
        try {
            ScraperRequestDto scraperRequestDto = new ScraperRequestDto(url);
            ScraperResponseDto response = scraperClientAdapter.scrape(scraperRequestDto);

            // Logging the successful request
            System.out.printf("Scraping successful for URL: %s\n", url);

            return Optional.ofNullable(ScrapedDataDomain.builder()
                    .price(response.getPrice())
                    .image(response.getImg_url())
                    .name(response.getName())
                    .platform(response.getPlatform())
                    .productPk(response.getProduct_key())
                    .url(response.getUrl())
                    .brand(response.getBrand())
                    .category(response.getCategory())
                    .fittingPart(response.getFitting_part())
                    .build());
        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("url", url);
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return Optional.ofNullable(null);
        }
    }

    public FittingResponseDto fitting(String userId, String clothesClass, MultipartFile itemImage, String userImage, String fcmToken, String clothesPk, String clohtesPlatform) {
        try {
            System.out.printf("class = %s\n",clothesClass);
            FittingResponseDto response = scraperClientAdapter.fitting(userId, clothesClass,fcmToken,clothesPk, clohtesPlatform,userImage, itemImage);
            return response;
        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return null;
        }
    }
}
