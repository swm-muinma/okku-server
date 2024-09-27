package kr.okku.server.controller;

import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.ReviewDomain;
import kr.okku.server.dto.controller.pick.*;
import kr.okku.server.dto.controller.review.ProductReviewDto;
import kr.okku.server.service.PickService;
import kr.okku.server.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestBody NewPickRequest request
    ) {
        String userId = userDetails.getUsername();
        try {
            PickDomain pick = pickService.createPick(userId, request.getUrl());
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
            @RequestBody DeletePicksRequest request
    ) {
        String userId = userDetails.getUsername();
        try {
            pickService.deletePicks(userId, request.getPickIds(), request.getCartId(), request.isDeletePermenant());
            System.out.printf("Request successful - UserId: %s, Deleted Picks: %s%n", userId, request.getPickIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Deleted Picks: %s, Error: %s%n", userId, request.getPickIds(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("")
    public ResponseEntity<UserPicksResponseDTO> getMyPicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String cartId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = userDetails.getUsername();
        try {
            UserPicksResponseDTO response = pickService.getMyPicks(userId, cartId, page, size);
            System.out.printf("Request successful - UserId: %s, CartId: %s, Page: %d, Size: %d%n", userId, cartId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, CartId: %s, Error: %s%n", userId, cartId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
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
            @RequestBody MovePicksRequest request
    ) {
        String userId = userDetails.getUsername();
        try {
            pickService.movePicks(userId, request.getPickIds(), request.getSourceCartId(), request.getDestinationCartId(), request.isDeleteFromOrigin());
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
