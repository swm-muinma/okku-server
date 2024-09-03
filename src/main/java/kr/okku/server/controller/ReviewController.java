package kr.okku.server.controller;
import jakarta.servlet.http.HttpServletRequest;
import kr.okku.server.dto.controller.review.ReviewRequest;
import kr.okku.server.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> getReviewWithoutLogin(@RequestBody ReviewRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = httpRequest.getRemoteAddr();
        }
        String userAgentString = httpRequest.getHeader("User-Agent");

        try {
            System.out.println("call getReviews Without login");
            var result = reviewService.getReviewsWithoutLogin(request.getProductPk(), request.getPlatform(), request.getOkkuId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
