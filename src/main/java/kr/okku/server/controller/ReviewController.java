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
            var result = reviewService.getReviewsWithoutLogin(request.getProductPk(), request.getPlatform(), request.getOkkuId());
            System.out.printf("Request successful - ProductPk: %s, Platform: %s, OkkuId: %s",
                    request.getProductPk(), request.getPlatform(), request.getOkkuId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - ProductPk: %s, Platform: %s, OkkuId: %s, Error: %s%n",
                    request.getProductPk(), request.getPlatform(), request.getOkkuId(), e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
