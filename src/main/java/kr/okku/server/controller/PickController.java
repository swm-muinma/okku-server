package kr.okku.server.controller;

import kr.okku.server.domain.PickDomain;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.dto.controller.review.ProductReviewDto;
import kr.okku.server.service.PickService;
import kr.okku.server.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/picks")
public class PickController {

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
        try {
            PickDomain pick = pickService.createPick(userId, request);
            System.out.printf("Request successful - UserId: %s, New Pick URL: %s%n", userId, request.getUrl());
            return ResponseEntity.ok(pick);
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, New Pick URL: %s, Error: %s%n", userId, request.getUrl(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deletePicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DeletePicksRequestDto request
    ) {
        String userId = userDetails.getUsername();
        try {
            pickService.deletePicks(userId, request);
            System.out.printf("Request successful - UserId: %s, Deleted Picks: %s%n", userId, request.getPickIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Deleted Picks: %s, Error: %s%n", userId, request.getPickIds(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("")
    public ResponseEntity<UserPicksResponseDto> getMyPicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String cartId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = userDetails.getUsername();
        GetMyPickRequestDto requestDto = new GetMyPickRequestDto();
        requestDto.setPage(page);
        requestDto.setSize(size);
        requestDto.setCartId(cartId);
            UserPicksResponseDto response = pickService.getMyPicks(userId, requestDto);
            System.out.printf("Request successful - UserId: %s, CartId: %s, Page: %d, Size: %d%n", userId, cartId, page, size);
            return ResponseEntity.ok(response);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ProductReviewDto> getReviews(
            @RequestParam String pickId
    ) {
        try {
            ProductReviewDto reviews = reviewService.getReviews(pickId);
            System.out.printf("Request successful - PickId: %s%n", pickId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            System.err.printf("Request failed - PickId: %s, Error: %s%n", pickId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("")
    public ResponseEntity<Void> movePicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MovePicksRequestDto request
    ) {
        String userId = userDetails.getUsername();
        try {
            pickService.movePicks(userId, request);
            System.out.printf("Request successful - UserId: %s, Moved Picks: %s%n", userId, request.getPickIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Moved Picks: %s, Error: %s%n", userId, request.getPickIds(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("fitting")
    public ResponseEntity<PickFittingResponseDto> getFitting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String pickId
    ){
        try {
            PickFittingResponseDto reviews = pickService.getFitting(pickId);
            System.out.printf("Request successful - PickId: %s%n", pickId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            System.err.printf("Request failed - PickId: %s, Error: %s%n", pickId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
