package kr.okku.server.service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import kr.okku.server.adapters.oauth.apple.AppleClientAdapter;
import kr.okku.server.adapters.oauth.apple.AppleOauthAdapter;
import kr.okku.server.adapters.persistence.RefreshPersistenceAdapter;
import kr.okku.server.adapters.persistence.UserPersistenceAdapter;
import kr.okku.server.domain.UserDomain;
import kr.okku.server.dto.oauth.ApplePublicKey;
import kr.okku.server.dto.oauth.ApplePublicKeys;
import kr.okku.server.dto.oauth.AppleTokenParser;
import kr.okku.server.dto.oauth.AppleTokenResponseDto;
import kr.okku.server.enums.RoleEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.security.JwtTokenProvider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import org.bouncycastle.openssl.PEMParser;

@Service
public class Oauth2Service {

    private final UserPersistenceAdapter userPersistenceAdapter;

    private final RefreshPersistenceAdapter refreshPersistenceAdapter;

    private final JwtTokenProvider jwtTokenProvider;
    private final AppleClientAdapter appleClientAdapter;
    private final AppleTokenParser appleTokenParser;

    private final AppleOauthAdapter appleOauthAdapter;

    @Autowired
    public Oauth2Service(RefreshPersistenceAdapter refreshPersistenceAdapter, UserPersistenceAdapter userPersistenceAdapter,
                         JwtTokenProvider jwtTokenProvider, AppleClientAdapter appleClientAdapter, AppleTokenParser appleTokenParser, AppleOauthAdapter appleOauthAdapter) {
        this.refreshPersistenceAdapter = refreshPersistenceAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.jwtTokenProvider = jwtTokenProvider;
        this.appleClientAdapter = appleClientAdapter;
        this.appleTokenParser = appleTokenParser;
        this.appleOauthAdapter = appleOauthAdapter;
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
    @Value("${spring.oauth.apple.client-id}")
    private String appleClientId;

    @Value("${spring.oauth.apple.team-id}")
    private String appleTeamId;

    @Value("${spring.oauth.apple.key-id}")
    private String appleKeyId;

    @Value("${spring.oauth.apple.redirect-uri}")
    private String appleRedirectUri;

    @Value("${spring.oauth.apple.private-key}")
    private String applePrivateKey;

    @Value("${spring.oauth.apple.aud}")
    private String aud;
    private final RestTemplate restTemplate = new RestTemplate();



    // Retrieve the redirect URL for the platform
    public String getRedirect(String platform) {
        if ("apple".equalsIgnoreCase(platform)) {
            return String.format(
                    "https://appleid.apple.com/auth/authorize?client_id=%s&response_type=code&redirect_uri=%s",
                    appleClientId, appleRedirectUri
            );
        }

        if ("kakao".equalsIgnoreCase(platform)) {
            return String.format("https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                    kakaoClientId, kakaoRedirectUri);
        }
        throw new ErrorDomain(ErrorCode.INVALID_PARAMS);
    }

    private static final String SIGN_ALGORITHM_HEADER = "alg";
    private static final String KEY_ID_HEADER = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;


    public PublicKey generate(Map<String, String> headers, ApplePublicKeys publicKeys) {
        // id_token에서 추출한 alg, kid와 일치하는 alg, kid를 가진 publicKey
        ApplePublicKey applePublicKey = publicKeys.getMatchingKey(
                headers.get(SIGN_ALGORITHM_HEADER),
                headers.get(KEY_ID_HEADER)
        );
        return generatePublicKey(applePublicKey);
    }

    // publicKey를 통해 RSAPublicKey 생성 & JWS E256 Signature 검증
    private PublicKey generatePublicKey(ApplePublicKey applePublicKey) {
        byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.n());
        byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.e());

        // publicKey의 n, e 값으로 RSAPublicKeySpec 생성
        BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            // publicKey의 kty 값으로 KeyFactory 생성
            KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.kty());
            // 생성한 KeyFactory와 PublicKeySpec으로 RSAPublicKey 생성
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new ErrorDomain(ErrorCode.RSA_ERROR);
        }
    }

    public Map<String, Object> appleLoginWithToken(String authorizationCode, String recommend) {
                AppleTokenResponseDto authToken = appleOauthAdapter.getAppleAuthToken(authorizationCode);
                Map<String, String> parseData = appleTokenParser.parseHeader(authToken.idToken());
                ApplePublicKeys applePublicKeys = appleClientAdapter.getApplePublicKeys();
                PublicKey validPublicKey = generate(parseData, applePublicKeys);
                Claims clames = extractClaims(authToken.idToken(), validPublicKey);
                String appleId = clames.get("sub").toString();
                return processingAppleLogin(appleId, recommend, "아기 오리");
    }

    private Map<String, Object> handleAppleLogin(String authorizationCode) {
        AppleTokenResponseDto authToken = appleOauthAdapter.getAppleAuthToken(authorizationCode);
        Map<String, String> parseData = appleTokenParser.parseHeader(authToken.idToken());
        ApplePublicKeys applePublicKeys = appleClientAdapter.getApplePublicKeys();
        PublicKey validPublicKey = generate(parseData,applePublicKeys);
        Claims clames = extractClaims(authToken.idToken(),validPublicKey);
        String appleId = clames.get("sub").toString();
        String name = clames.get("name").toString();
        return processingAppleLogin(appleId,"",name);
    }

    public Claims extractClaims(String idToken, PublicKey publicKey) {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();

    }

    public Jws<Claims> validateAppleToken(String idToken) throws Exception {
        // Apple의 JWKs URL에서 공개 키를 가져옵니다.
        String jwksUrl = "https://appleid.apple.com/auth/keys";
        ResponseEntity<String> response = restTemplate.getForEntity(jwksUrl, String.class);

        // JWKSet으로 Apple의 키를 파싱
        JWKSet jwkSet = JWKSet.parse(response.getBody());
        List<JWK> jwks = jwkSet.getKeys();

        // ID Token을 파싱하여 kid를 가져옴
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        String kid = signedJWT.getHeader().getKeyID();

        // kid에 맞는 JWK를 찾음
        JWK matchingKey = jwks.stream()
                .filter(jwk -> jwk.getKeyID().equals(kid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Matching JWK not found for kid: " + kid));

        // 공개 키를 사용하여 ID Token의 서명을 검증
        if (!(matchingKey instanceof RSAKey)) {
            throw new RuntimeException("JWK is not an RSA key");
        }

        RSAPublicKey publicKey = ((RSAKey) matchingKey).toRSAPublicKey();

        // 검증된 JWT를 파싱하여 클레임을 추출합니다.
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken);

        return claims;
    }
    // Handle OAuth2 login
    @Transactional
    public Map<String, Object> oauth2Login(String platform, String authorizationCode) {
        if ("apple".equalsIgnoreCase(platform)) {
            return handleAppleLogin(authorizationCode);
        }
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
        UserDomain user = userPersistenceAdapter.findByKakaoId(kakaoId).orElse(null);
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

    // Apple 로그인 처리
    public Map<String, Object> processingAppleLogin(String appleId, String recomend, String nickName) {
        boolean isNewUser = false;
        UserDomain user = userPersistenceAdapter.findByAppleId(appleId).orElse(null);
        if (user == null) {
            user = UserDomain.builder()
                    .name(nickName)
                    .build();
            user.setAppleId(appleId);
            if (recomend != null && !recomend.isEmpty() && recomend!="") {
                UserDomain recomendUser = userPersistenceAdapter.findById(recomend).orElse(null);
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
