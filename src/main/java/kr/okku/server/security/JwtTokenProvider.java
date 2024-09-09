package kr.okku.server.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.acess-token.secret}")
    private String accessSecretKey;

    @Value("${spring.jwt.acess-token.expiration}")
    private long accessValidityInMilliseconds;

    @Value("${spring.jwt.refresh-token.secret}")
    private String refreshSecretKey;

    @Value("${spring.jwt.refresh-token.expiration}")
    private long refreshValidityInMilliseconds;

    // 토큰 생성 - 권한 포함
    public String createAccessToken(String userId, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles); // 권한을 claims에 추가

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();
    }

    public String createRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();
    }

    // 토큰에서 사용자 아이디 추출
    public String getUserIdFromAccessToken(String token) {
        System.out.println("d");
        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰에서 권한 추출
    public List<String> getRolesFromAccessToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody();
        return claims.get("roles", List.class);
    }

    // 토큰 유효성 검사
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromRefreshToken(String token) {
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 요청에서 헤더에서 토큰을 가져오기
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody();
        return (List<String>) claims.get("roles");
    }

    public UserDetails getUserDetailsFromToken(String token) {
        String userId = getUserIdFromAccessToken(token);
        List<String> roles = getRolesFromToken(token);

        return new org.springframework.security.core.userdetails.User(
                userId,
                "", // 비밀번호가 필요하다면 설정
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }

    public Map<String, String> parseHeaders(String token) throws JsonProcessingException {
        String header = token.split("\\.")[0];
        return new ObjectMapper().readValue(decodeHeader(header), Map.class);
    }

    public String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }

    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
