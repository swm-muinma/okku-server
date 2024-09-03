package kr.okku.server.controller;

import kr.okku.server.domain.PickDomain;
import kr.okku.server.domain.ReviewDomain;
import kr.okku.server.dto.controller.pick.DeletePicksRequest;
import kr.okku.server.dto.controller.pick.MovePicksRequest;
import kr.okku.server.dto.controller.pick.NewPickRequest;
import kr.okku.server.dto.controller.pick.UserPicksResponseDTO;
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
        return ResponseEntity.ok(pickService.createPick(userId, request.getUrl()));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deletePicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DeletePicksRequest request
    ) {
        String userId = userDetails.getUsername();
        pickService.deletePicks(userId, request.getPickIds(), request.getCartId(), request.isDeletePermenant());
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<UserPicksResponseDTO> getMyPicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String cartId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = userDetails.getUsername();
        System.out.println(userId);
        return ResponseEntity.ok(pickService.getMyPicks(userId, cartId, page, size));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ProductReviewDto> getReviews(
            @RequestParam String pickId
    ) {
        return ResponseEntity.ok(reviewService.getReviews(pickId));
    }

    @PatchMapping("/")
    public ResponseEntity<Void> movePicks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MovePicksRequest request
    ) {
        String userId = userDetails.getUsername();
        pickService.movePicks(userId, request.getPickIds(), request.getSourceCartId(), request.getDestinationCartId(), request.isDeleteFromOrigin());
        return ResponseEntity.ok().build();
    }
}
