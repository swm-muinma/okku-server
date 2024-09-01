package kr.okku.server.controller;

import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.service.Oauth2Service;
import kr.okku.server.service.PickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    public Oauth2Controller(Oauth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    // Handle Kakao login with token
    @PostMapping("/kakao/login")
    public ResponseEntity<Map<String, Object>> kakaoLoginWithToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String recommend = request.get("recommend");

        if (token == null) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
        }

        Map<String, Object> result = oauth2Service.kakaoLoginWithToken(token, recommend);
        return ResponseEntity.ok(result);
    }

    // Handle OAuth2 login request
    @GetMapping("/{platform}/login")
    public ResponseEntity<Map<String, Object>> oauth2Login(
            @PathVariable String platform,
            @RequestParam String code) {
        Map<String, Object> result = oauth2Service.oauth2Login(platform, code);
        return ResponseEntity.ok(result);
    }
}
