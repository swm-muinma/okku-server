package kr.okku.server.controller;

import kr.okku.server.domain.Log.ControllerLogEntity;
import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.domain.PickDomain;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.dto.controller.review.ProductReviewDto;
import kr.okku.server.dto.service.CreatePickDto;
import kr.okku.server.service.PickService;
import kr.okku.server.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/picks")
public class PickController {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final PickService pickService;
    private final ReviewService reviewService;

    public PickController(PickService pickService, ReviewService reviewService) {
        this.pickService = pickService;
        this.reviewService = reviewService;
    }

    @PostMapping("/new")
    public ResponseEntity<PickDomain> createPick(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NewPickRequestDto request
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/new","POST","요청 시작").toJson());
            CreatePickDto createPickDto = pickService.createPick(traceId,userId, request);
            PickDomain pick = createPickDto.getPickDomain();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/new","POST","요청 종료").toJson());
            return ResponseEntity.ok(pick);
    }

    @PostMapping("/v1/new")
    public ResponseEntity<CreatePickResponseDto> createPickForRawReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NewPickRequestDto request
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/v1/new","POST","요청 시작").toJson());
        CreatePickResponseDto pick = pickService.createPickForRawReviews(traceId,userId, request);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/v1/new","POST","요청 종료").toJson());
        return ResponseEntity.ok(pick);
    }

    @PostMapping("/v1/raw-reviews")
    public ResponseEntity<GetNextPageForRawReviewsResponseDto> getNextPageForRawReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GetNextPageForRawReviewsRequestDto request
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        traceId.setId(request.getTraceId());
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/v1/raw-reviews","POST","요청 시작").toJson());
        GetNextPageForRawReviewsResponseDto pick = pickService.getNextPageForRawReviews(traceId,request);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/v1/raw-reviews","POST","요청 종료").toJson());
        return ResponseEntity.ok(pick);
    }

    @PostMapping("/v1/raw-reviews/submit")
    public ResponseEntity<SubmitRawReviewsResponseDto> submitRawReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SubmitRawReviewsRequestDto request
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        traceId.setId(request.getTraceId());
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/v1/raw-reviews/done","POST","요청 시작").toJson());
        SubmitRawReviewsResponseDto pick = pickService.submitRawReviews(traceId,request);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/v1/raw-reviews/done","POST","요청 종료").toJson());
        return ResponseEntity.ok(pick);
    }
    @PostMapping("/delete")
    public ResponseEntity<Void> deletePicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DeletePicksRequestDto request
    ) {
        String userId = userDetails.getUsername();

        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/delete","POST","요청 시작").toJson());
            pickService.deletePicks(traceId,userId, request);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/delete","POST","요청 종료").toJson());
            return ResponseEntity.ok().build();

    }

    @GetMapping("")
    public ResponseEntity<UserPicksResponseDto> getMyPicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String cartId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks&cartId="+cartId,"GET","요청 시작").toJson());
        GetMyPickRequestDto requestDto = new GetMyPickRequestDto();
        requestDto.setPage(page);
        requestDto.setSize(size);
        requestDto.setCartId(cartId);
            UserPicksResponseDto response = pickService.getMyPicks(traceId,userId, requestDto);

        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks&cartId="+cartId,"GET","요청 종료").toJson());
            return ResponseEntity.ok(response);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ProductReviewDto> getReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String pickId
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/reviews&pickId="+pickId,"GET","요청 시작").toJson());
        ProductReviewDto reviews = reviewService.getReviews(pickId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks/reviews&pickId="+pickId,"GET","요청 종료").toJson());
        return ResponseEntity.ok(reviews);

    }

    @PatchMapping("")
    public ResponseEntity<Void> movePicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MovePicksRequestDto request
    ) {
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks","PATCH","요청 시작").toJson());
        pickService.movePicks(traceId,userId, request);

        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks","PATCH","요청 종료").toJson());
        return ResponseEntity.ok().build();

    }

    @GetMapping("fitting")
    public ResponseEntity<PickFittingResponseDto> getFitting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String pickId
    ){
        String userId = userDetails.getUsername();
        TraceId traceId = new TraceId();
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks?pickId="+pickId,"GET","요청 시작").toJson());
            PickFittingResponseDto reviews = pickService.getFitting(traceId,pickId);
        log.info("{}",new ControllerLogEntity(traceId,userId,"/picks?pickId="+pickId,"GET","요청 종료").toJson());
            return ResponseEntity.ok(reviews);

    }
}
