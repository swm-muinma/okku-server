package kr.okku.server.adapters.scraper;

import kr.okku.server.dto.adapter.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "scraper", url = "${spring.msa.scraper.uri}")
public interface ScraperClientAdapter {

    @PostMapping(value = "/v2/scrap", consumes = MediaType.APPLICATION_JSON_VALUE)
    ScraperResponseDto scrape(@RequestBody ScraperRequestDto scraperRequestDto);

    @PostMapping(value = "/v2/fitting", consumes = MediaType.APPLICATION_JSON_VALUE)
    FittingResponseDto fitting(
            @RequestBody FittingRequestDto fittingRequestDto
            );

    @PostMapping(value = "/v2/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    ValidateResponseDto validate(
            @RequestBody ValidateRequestDto validateRequestDto
    );
}