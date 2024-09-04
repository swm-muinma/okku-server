package kr.okku.server.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.okku.server.adapters.persistence.RefreshPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.refresh.RefreshRepository;
import kr.okku.server.dto.controller.refresh.TokenResponse;
import kr.okku.server.enums.RoleEnum;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RefreshService {

    private final RefreshPersistenceAdapter refreshPersistenceAdapter;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshService(RefreshPersistenceAdapter refreshPersistenceAdapter, JwtTokenProvider jwtTokenProvider) {
        this.refreshPersistenceAdapter = refreshPersistenceAdapter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse updateRefresh(String refreshToken) {
        boolean isExist = refreshPersistenceAdapter.isExist(refreshToken);
        if (!isExist) {
            throw new ErrorDomain(ErrorCode.REFRESH_INVALID);
        }

        try {
            List<String> roles = new ArrayList<>();
            roles.add(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
            String userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.createAccessToken(userId,roles);

            String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
            refreshPersistenceAdapter.update(refreshToken,newRefreshToken);

            return new TokenResponse(newAccessToken, newRefreshToken);

        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.REFRESH_INVALID);
        }
    }
}
