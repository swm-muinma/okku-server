package kr.okku.server.service;

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
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Oauth2Service {

    private final UserPersistenceAdapter userPersistenceAdapter;

    private final RefreshPersistenceAdapter refreshPersistenceAdapter;

    @Autowired
    public Oauth2Service(RefreshPersistenceAdapter refreshPersistenceAdapter, UserPersistenceAdapter userPersistenceAdapter,
                       ScraperAdapter scraperAdapter, UserRepository userRepository) {
        this.refreshPersistenceAdapter = refreshPersistenceAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
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
    public Map<String, Object> kakaoLoginWithToken(String accessToken, String recommend) {
        Map<String, Object> result = new HashMap<>();
        boolean isNewUser = false;

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        Map<String, Object> userResponse = restTemplate.getForObject(userInfoUrl, Map.class, headers);
        String kakaoId = String.valueOf(userResponse.get("id"));

        if (kakaoId == null) {
            throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
        }

        UserDomain user = userPersistenceAdapter.findByKakaoId(kakaoId).get();
        if (user == null) {
            String nickname = (String) ((Map<String, Object>) userResponse.get("kakao_account")).get("nickname");
            user = UserDomain.builder()
                    .name(nickname)
                    .build();
            user.setKakaoId(kakaoId);
            if (recommend != null && !recommend.isEmpty()) {
                UserDomain recomendUser = userPersistenceAdapter.findById(recommend).get();
                recomendUser.setIsPremium(true);
                userPersistenceAdapter.save(recomendUser);
            }
            user = userPersistenceAdapter.save(user);
            isNewUser = true;
        }

        String newAccessToken = Jwts.builder()
                .setSubject(user.getId())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(user.getId())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        refreshPersistenceAdapter.save(refreshToken);

        result.put("accessToken", newAccessToken);
        result.put("refreshToken", refreshToken);
        result.put("isNewUser", isNewUser);
        return result;
    }
}
