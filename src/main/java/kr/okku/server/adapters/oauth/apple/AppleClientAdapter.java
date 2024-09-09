package kr.okku.server.adapters.oauth.apple;

import kr.okku.server.dto.oauth.ApplePublicKeys;
import kr.okku.server.dto.oauth.AppleTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "apple-oauth", url = "https://appleid.apple.com")
public interface AppleClientAdapter {

    @PostMapping("/auth/token")
    AppleTokenResponseDto appleAuth(
            @RequestParam("client_id") String clientId,
            @RequestParam("code") String code,
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_secret") String clientSecret);
    @GetMapping("/auth/keys")
    ApplePublicKeys getApplePublicKeys();
}