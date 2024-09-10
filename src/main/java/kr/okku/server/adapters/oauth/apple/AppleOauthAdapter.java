package kr.okku.server.adapters.oauth.apple;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.okku.server.dto.oauth.AppleTokenResponseDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppleOauthAdapter {

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

    private final AppleClientAdapter appleClientAdapter;

    public AppleOauthAdapter(AppleClientAdapter appleClientAdapter) {
        this.appleClientAdapter = appleClientAdapter;
    }


    public AppleTokenResponseDto getAppleAuthToken(String authorizationCode){
        String clientSecret = createClientSecret();
        return appleClientAdapter.appleAuth(appleClientId, authorizationCode, "authorization_code", clientSecret);

    }

    public void revoke(AppleTokenResponseDto authToken){
        String clientSecret = createClientSecret();
        appleClientAdapter.appleRevoke(appleClientId,clientSecret,authToken.accessToken());
    }
    private String createClientSecret() {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("alg", "ES256");
        jwtHeader.put("kid", appleKeyId);

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(appleTeamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience(aud)
                .setSubject(appleClientId)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            Reader pemReader = new StringReader(applePrivateKey);
            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            return converter.getPrivateKey(object);
        }catch (Exception e){
            throw new ErrorDomain(ErrorCode.APPLE_LOGIN_FAILED);
        }
    }
}
