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
            System.out.printf("Scrape request received from IP: %s, User-Agent: %s%n", ip, userAgentString);
            var result = reviewService.getItemInfoWithoutLogin(request.getUrl(), request.getOkkuId());
            System.out.println("Request successful - Scrape item: " + request.getUrl());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - Scrape item: %s, Error: %s%n", request.getUrl(), e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
