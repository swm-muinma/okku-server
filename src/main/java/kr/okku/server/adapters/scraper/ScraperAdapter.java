package kr.okku.server.adapters.scraper;

import io.sentry.Sentry;
import kr.okku.server.domain.Log.*;
import kr.okku.server.domain.ScrapedDataDomain;
import kr.okku.server.dto.adapter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScraperAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final ScraperClientAdapter scraperClientAdapter;

    @Autowired
    public ScraperAdapter(ScraperClientAdapter scraperClientAdapter) {
        this.scraperClientAdapter = scraperClientAdapter;
    }
    public Optional<ScrapedDataDomain> scrape(TraceId traceId,String url) {
        log.info("{}",new ScraperLogEntity(traceId,url,"스크랩 시작").toJson());
        try {
            ScraperRequestDto scraperRequestDto = new ScraperRequestDto(url);
            ScraperResponseDto response = scraperClientAdapter.scrape(scraperRequestDto);
            log.info("{}",new ScraperReponseLogEntity(traceId,"스크랩 종료").toJson());
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
                scope.setExtra("traceId", traceId.getId());
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return Optional.empty();
        }
    }

    public FittingResponseDto fitting(TraceId traceId,String userId, String clothesClass, String itemImage, String userImage, String fcmToken, String clothesPk, String clohtesPlatform) {
        try {
            FittingRequestDto fittingRequestDto = new FittingRequestDto(userId, clothesClass,fcmToken,clothesPk, clohtesPlatform,userImage, itemImage);
            log.info("{}",new FittingRequestLogEntity(traceId,"피팅 요청 시작").toJson());
            FittingResponseDto response = scraperClientAdapter.fitting(fittingRequestDto);
            log.info("{}",new FittingResponseLogEntity(traceId,response.getId(),"피팅 요청 종료").toJson());
            return response;
        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("error_message", e.getMessage());
                scope.setExtra("traceId", traceId.getId());
                Sentry.captureException(e);
            });
            return null;
        }
    }

    public boolean canFitting(TraceId traceId, String userImage) {
        log.info("{}",new CanFittingLogEntity(traceId,userImage,"사진 판별 시작").toJson());
        try {
            ValidateRequestDto validateRequestDto = new ValidateRequestDto(userImage);
            ValidateResponseDto response = scraperClientAdapter.validate(validateRequestDto);
            log.info("{}",new CanFittingResponseLogEntity(traceId,userImage,response.getStatus(),"사진 판별 종료").toJson());
            return response.getStatus().equals("success");
        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("traceId", traceId.getId());
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return false;
        }
    }

    public String crateInsight(String traceId, String pk, String platform) {
        try {
            CreateInsightRequestDto createInsightRequestDto = new CreateInsightRequestDto(traceId,pk,platform);
            CreateInsightResponseDto response = scraperClientAdapter.createInsight(createInsightRequestDto);
            return response.getStatus();
        } catch (Exception e) {
            Sentry.withScope(scope -> {
                scope.setExtra("traceId", traceId);
                scope.setExtra("error_message", e.getMessage());
                Sentry.captureException(e);
            });
            return "fail";
        }
    }
}
