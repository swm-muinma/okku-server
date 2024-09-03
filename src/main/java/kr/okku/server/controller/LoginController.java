package kr.okku.server.controller;

import kr.okku.server.enums.RoleEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.security.JwtTokenProvider;
import kr.okku.server.service.Oauth2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final Oauth2Service oauth2Service;

    private final JwtTokenProvider jwtTokenProvider;

    public LoginController(Oauth2Service oauth2Service, JwtTokenProvider jwtTokenProvider) {
        this.oauth2Service = oauth2Service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Handle Kakao login with token
    @PostMapping("/app/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLoginWithToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String recommend = request.get("recommend");

        if (token == null) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
        }

        Map<String, Object> result = oauth2Service.kakaoLoginWithToken(token, recommend);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test/{userId}")
    public String test(@PathVariable String userId){
        List<String> rolse = new ArrayList<>();
        rolse.add(RoleEnum.USER.getValue());
        return jwtTokenProvider.createAccessToken(userId, rolse);
    }

    // Handle OAuth2 login request
    @GetMapping("/oauth2/code/{platform}")
    public ResponseEntity<Map<String, Object>> oauth2Login(
            @PathVariable String platform,
            @RequestParam String code) {
        Map<String, Object> result = oauth2Service.oauth2Login(platform, code);
        return ResponseEntity.ok(result);
    }
}
