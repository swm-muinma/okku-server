package kr.okku.server.controller;

import kr.okku.server.domain.Log.ControllerLogEntity;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.fitting.FittingRequestDto;
import kr.okku.server.dto.controller.fitting.FittingResultDto;
import kr.okku.server.dto.controller.fitting.GetFittingListResponseDto;
import kr.okku.server.dto.controller.fitting.LegacyFittingRequestDto;
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
@RequestMapping("/fitting")
public class LegacyFittingController {

    private final FittingService fittingService;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    public LegacyFittingController(FittingService fittingService) {
        this.fittingService = fittingService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fitting(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute LegacyFittingRequestDto requestDto) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/fitting","POST","요청 시작").toJson());
        var result = fittingService.legacyFitting(traceId, userId, requestDto);

        log.info("{}",new ControllerLogEntity(traceId,userId,"/fitting","POST","요청 종료").toJson());
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<GetFittingListResponseDto> getFittingList(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/fitting","GET","요청 시작").toJson());
        GetFittingListResponseDto result = fittingService.getFittingList(traceId, userId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/fitting","GET","요청 종료").toJson());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/recent")
    public ResponseEntity<FittingResultDto> getRecentlyFittingItem(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/fitting/recent","GET","요청 시작").toJson());
        FittingResultDto result = fittingService.getNowOne(traceId,userId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/fitting/recent","GET","요청 종료").toJson());
        return ResponseEntity.ok(result);
    }
}