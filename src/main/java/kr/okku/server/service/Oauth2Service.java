package kr.okku.server.service;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.okku.server.adapters.persistence.CartPersistenceAdapter;
import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.RefreshPersistenceAdapter;
import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.refresh.RefreshRepository;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.enums.FormEnum;
import kr.okku.server.enums.RoleEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Oauth2Service {

    private final UserPersistenceAdapter userPersistenceAdapter;

    private final RefreshPersistenceAdapter refreshPersistenceAdapter;

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public Oauth2Service(RefreshPersistenceAdapter refreshPersistenceAdapter, UserPersistenceAdapter userPersistenceAdapter,
                         JwtTokenProvider jwtTokenProvider) {
        this.refreshPersistenceAdapter = refreshPersistenceAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Value("${spring.oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.jwt.acess-token.secret}")
    private String secretKey;

    @Value("${spring.jwt.acess-token.expiration}")
    private long expirationTime;

    @Value("${spring.jwt.refresh-token.expiration}")
    private long refreshTokenExpirationTime;

    private final RestTemplate restTemplate = new RestTemplate();

    // Retrieve the redirect URL for the platform
    public String getRedirect(String platform) {
        if ("kakao".equalsIgnoreCase(platform)) {
            return String.format("https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                    kakaoClientId, kakaoRedirectUri);
        }
        throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
    }

    // Handle OAuth2 login
    @Transactional
    public Map<String, Object> oauth2Login(String platform, String authorizationCode) {
        if ("kakao".equalsIgnoreCase(platform)) {
            return handleKakaoLogin(authorizationCode);
        }
        throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
    }

    // Handle Kakao login logic
    private Map<String, Object> handleKakaoLogin(String authorizationCode) {
            String tokenUrl = "https://kauth.kakao.com/oauth/token";
            Map<String, String> tokenRequest = new HashMap<>();
            tokenRequest.put("grant_type", "authorization_code");
            tokenRequest.put("client_id", kakaoClientId);
            tokenRequest.put("redirect_uri", kakaoRedirectUri);
            tokenRequest.put("code", authorizationCode);

            Map<String, Object> tokenResponse = restTemplate.postForObject(tokenUrl, tokenRequest, Map.class);
            String accessToken = (String) tokenResponse.get("access_token");

            return kakaoLoginWithToken(accessToken, "");

    }

    // Kakao login with token and optional recommendation
    @Transactional
    public Map<String, Object> kakaoLoginWithToken(String accessToken, String recomend) {
        // Kakao API URL
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        // HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // HttpEntity 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 보내기
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> userResponse = response.getBody();

        if (userResponse == null || userResponse.get("id") == null) {
            throw new RuntimeException("Kakao ID not found from Kakao");
        }

        String kakaoId = userResponse.get("id").toString();

        boolean isNewUser = false;
        UserDomain user = userPersistenceAdapter.findByKakaoId(kakaoId).get();
        if (user == null) {
            String nickname = (String) ((Map<String, Object>) userResponse.get("kakao_account")).get("nickname");
            user = UserDomain.builder()
                    .name(nickname)
                    .build();
            user.setKakaoId(kakaoId);
            if (recomend != null && !recomend.isEmpty()) {
                UserDomain recomendUser = userPersistenceAdapter.findById(recomend).get();
                recomendUser.setIsPremium(true);
                userPersistenceAdapter.save(recomendUser);
            }
            user = userPersistenceAdapter.save(user);
            isNewUser = true;
        }

        List<String> roles = new ArrayList<>();
        roles.add(RoleEnum.USER.getValue());

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(),roles);

        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        refreshPersistenceAdapter.save(refreshToken);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accessToken", newAccessToken);
        responseMap.put("refreshToken", refreshToken);
        responseMap.put("isNewUser", isNewUser);

        return responseMap;
    }
}
