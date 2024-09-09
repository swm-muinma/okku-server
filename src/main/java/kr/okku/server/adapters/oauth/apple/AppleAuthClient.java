package kr.okku.server.adapters.oauth.apple;

import kr.okku.server.dto.oauth.ApplePublicKeyResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class AppleAuthClient {

    private final RestTemplate restTemplate;

    // Apple의 공개 키를 가져오는 URL (application.properties 또는 application.yml에 설정된 값)
    private final String appleAuthPublicKeyUrl;

    // 생성자 주입
    public AppleAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.appleAuthPublicKeyUrl = "https://appleid.apple.com/auth/keys"; // 혹은 환경 변수로 주입
    }


    public ApplePublicKeyResponse getAppleAuthPublicKey() {
        // RestTemplate을 사용해 Apple의 공개 키 요청
        ResponseEntity<ApplePublicKeyResponse> response = restTemplate.getForEntity(
                appleAuthPublicKeyUrl,
                ApplePublicKeyResponse.class
        );

        // 응답 검증 및 반환
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch Apple public key");
        }
    }
}
