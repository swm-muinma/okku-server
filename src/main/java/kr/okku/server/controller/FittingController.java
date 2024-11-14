package kr.okku.server.controller;

import kr.okku.server.domain.Log.ControllerLogEntity;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.fitting.*;
import kr.okku.server.service.FittingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/fitting")
public class FittingController {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final FittingService fittingService;

    @Autowired
    public FittingController(FittingService fittingService) {
        this.fittingService = fittingService;
    }

    @PostMapping()
    public ResponseEntity<?> fitting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FittingRequestDto request) {
            String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting","POST","요청 시작").toJson());
            var result = fittingService.fitting(traceId,userId, request);

        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting","POST","요청 종료").toJson());
            return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/validate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CanFittingResponseDto> canFitting(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute CanFittingRequestDto requestDto) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting/validate","POST","요청 시작").toJson());
        CanFittingResponseDto result = fittingService.canFitting(traceId,userId,requestDto);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting/validate","POST","요청 종료").toJson());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/validate-test")
    public ResponseEntity<String> validateTest(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        String result = fittingService.validateTest("https://user-images-caching.s3.ap-northeast-2.amazonaws.com/fa3ed155-21000009641.jpg");

        return ResponseEntity.ok(result);
    }
    @GetMapping()
    public ResponseEntity<GetFittingListResponseDto> getFittingList(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting","GET","요청 시작").toJson());
        GetFittingListResponseDto result = fittingService.getFittingList(traceId,userId);

        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting","GET","요청 종료").toJson());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/recent")
    public ResponseEntity<FittingResultDto> getRecentlyFittingItem(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting/recent","GET","요청 시작").toJson());
        FittingResultDto result = fittingService.getNowOne(traceId,userId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting/recent","GET","요청 종료").toJson());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCachingUserImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DeleteCachingUserImageRequestDto requestDto) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting/delete","POST","요청 시작").toJson());
        fittingService.deleteCachingUserImage(traceId,userId,requestDto);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/v2/fitting/delete","POST","요청 종료").toJson());
        return ResponseEntity.ok().build();
    }
}
