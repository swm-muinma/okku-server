package kr.okku.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.okku.server.dto.controller.review.ScrapeRequest;
import kr.okku.server.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/scrape")
public class ScrapeController {

    private final ReviewService reviewService;

    @Autowired
    public ScrapeController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> getItemInfoWithoutLogin(@RequestBody ScrapeRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = httpRequest.getRemoteAddr();
        }
        String userAgentString = httpRequest.getHeader("User-Agent");

        try {
            System.out.println("call getItem Without login");
            var result = reviewService.getItemInfoWithoutLogin(request.getUrl(), request.getOkkuId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
