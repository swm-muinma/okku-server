package kr.okku.server.controller;

import kr.okku.server.domain.PickDomain;
import kr.okku.server.dto.controller.pick.DeletePicksRequest;
import kr.okku.server.dto.controller.pick.UserPicksResponseDTO;
import kr.okku.server.service.PickService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/picks")
public class PickController {

    private final PickService pickService;

    public PickController(PickService pickService) {
        this.pickService = pickService;
    }

    @PostMapping("/new")
    public ResponseEntity<PickDomain> createPick(
            @RequestParam String userId,
            @RequestParam String url
    ) {
        return ResponseEntity.ok(pickService.createPick(userId, url));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deletePicks(
            @RequestBody DeletePicksRequest request
    ) {
        pickService.deletePicks(request.getUserId(), request.getPickIds(), request.getCartId(), request.isDeletePermenant());
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<UserPicksResponseDTO> getMyPicks(
            @RequestParam String userId,
            @RequestParam(required = false) String cartId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(pickService.getMyPicks(userId, cartId, page, size));
    }

//    @GetMapping("/reviews")
//    public ResponseEntity<List<ReviewDomain>> getReviews(
//            @RequestParam String pickId
//    ) {
//        return ResponseEntity.ok(pickService.getReviews(pickId));
//    }
//
//    @PatchMapping("/")
//    public ResponseEntity<Void> movePicks(
//            @RequestBody MovePicksRequest request
//    ) {
//        pickService.movePicks(request.getUserId(), request.getPickIds(), request.getSourceCartId(), request.getDestinationCartId(), request.isDeleteFromOrigin());
//        return ResponseEntity.ok().build();
//    }
}
