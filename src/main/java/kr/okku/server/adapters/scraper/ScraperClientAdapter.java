package kr.okku.server.adapters.scraper;

import kr.okku.server.dto.adapter.FittingResponseDto;
import kr.okku.server.dto.adapter.ScraperRequestDto;
import kr.okku.server.dto.adapter.ScraperResponseDto;
import kr.okku.server.dto.oauth.ApplePublicKeys;
import kr.okku.server.dto.oauth.AppleTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Component
@FeignClient(name = "scraper", url = "${spring.msa.scraper.uri}")
public interface ScraperClientAdapter {

    @PostMapping(value = "/scrap", consumes = MediaType.APPLICATION_JSON_VALUE)
    ScraperResponseDto scrape(@RequestBody ScraperRequestDto scraperRequestDto);

    @PostMapping(value = "/fitting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FittingResponseDto fitting(
            @RequestPart("user_pk") String userId,
            @RequestPart("clothes_class") String clothesClass,
            @RequestPart("human_img") MultipartFile userImage,
            @RequestPart("clothes_img") MultipartFile itemImage
    );
}