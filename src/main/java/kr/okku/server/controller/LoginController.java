package kr.okku.server.controller;

import kr.okku.server.domain.ReviewInsightDomain;
import kr.okku.server.dto.controller.refresh.TokenResponse;
import kr.okku.server.dto.controller.review.ProductReviewDto;
import kr.okku.server.enums.RoleEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.security.JwtTokenProvider;
import kr.okku.server.service.Oauth2Service;
import kr.okku.server.service.RefreshService;
import kr.okku.server.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/login")
public class LoginController {

    private final Oauth2Service oauth2Service;
    private final RefreshService refreshService;
    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginController(Oauth2Service oauth2Service, JwtTokenProvider jwtTokenProvider, RefreshService refreshService, ReviewService reviewService) {
        this.oauth2Service = oauth2Service;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshService = refreshService;
        this.reviewService = reviewService;
    }

    @PostMapping("/app/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLoginWithToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String recommend = request.get("recommend");

        try {
            if (token == null) {
                throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
            }
            Map<String, Object> result = oauth2Service.kakaoLoginWithToken(token, recommend);
            System.out.printf("Request successful - Kakao token: %s%n", token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - Kakao token: %s, Error: %s%n", token, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/app/apple")
    public ResponseEntity<Map<String, Object>> appleLoginWithToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String recommend = request.get("recommend");

        try {
            if (token == null) {
                throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
            }
            Map<String, Object> result = oauth2Service.appleLoginWithToken(token, recommend);
            System.out.printf("Request successful - Apple token: %s%n", token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - Apple token: %s, Error: %s%n", token, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/{userId}")
    public String test(@PathVariable String userId) {
        try {
            List<String> roles = new ArrayList<>();
            roles.add(RoleEnum.USER.getValue());
            String accessToken = jwtTokenProvider.createAccessToken(userId, roles);
            System.out.printf("Request successful - Test access token for userId: %s%n", userId);
            return accessToken;
        } catch (Exception e) {
            System.err.printf("Request failed - UserId: %s, Error: %s%n", userId, e.getMessage());
            return "Error generating token";
        }
    }

    @GetMapping("/review-test")
    public ProductReviewDto reviewTest() {
        try {
            ProductReviewDto result = reviewService.getReviewsWithoutLogin("140062026", "zigzag", "sada");
            System.out.println("Request successful - Review test");
            return result;
        } catch (Exception e) {
            System.err.printf("Request failed - Review test, Error: %s%n", e.getMessage());
            return null;
        }
    }

    @GetMapping("/oauth2/code/{platform}")
    public ResponseEntity<Map<String, Object>> oauth2Login(@PathVariable String platform, @RequestParam String code) {
        try {
            Map<String, Object> result = oauth2Service.oauth2Login(platform, code);
            System.out.printf("Request successful - OAuth2 login with platform: %s, Code: %s%n", platform, code);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - OAuth2 login with platform: %s, Code: %s, Error: %s%n", platform, code, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        try {
            if (refreshToken == null) {
                throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
            }
            TokenResponse result = refreshService.updateRefresh(refreshToken);
            System.out.printf("Request successful - Refresh token: %s%n", refreshToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.printf("Request failed - Refresh token: %s, Error: %s%n", refreshToken, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
