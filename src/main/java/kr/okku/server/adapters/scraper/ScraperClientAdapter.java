package kr.okku.server.adapters.scraper;

import kr.okku.server.dto.adapter.FittingRequestDto;
import kr.okku.server.dto.adapter.FittingResponseDto;
import kr.okku.server.dto.adapter.ScraperRequestDto;
import kr.okku.server.dto.adapter.ScraperResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Component
@FeignClient(name = "scraper", url = "${spring.msa.scraper.uri}")
public interface ScraperClientAdapter {

    @PostMapping(value = "/v2/scrap", consumes = MediaType.APPLICATION_JSON_VALUE)
    ScraperResponseDto scrape(@RequestBody ScraperRequestDto scraperRequestDto);

    @PostMapping(value = "/v2/fitting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FittingResponseDto fitting(
            @RequestPart("user_pk") String userId,
            @RequestPart("clothes_class") String clothesClass,
            @RequestPart("fcm_token") String fcmToken,
            @RequestPart("clothes_pk") String clothesPk,
            @RequestPart("clothes_platform") String clothesPlatform,
            @RequestPart("human_img_url") String userImage,  // 이미지 URL 또는 이미지 데이터를 처리
            @RequestPart("clothes_img") MultipartFile itemImage
    );
}