package kr.okku.server.service;

import kr.okku.server.adapters.persistence.RefreshPersistenceAdapter;
import kr.okku.server.dto.controller.refresh.TokenResponseDto;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import kr.okku.server.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RefreshService {

    private final RefreshPersistenceAdapter refreshPersistenceAdapter;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshService(RefreshPersistenceAdapter refreshPersistenceAdapter, JwtTokenProvider jwtTokenProvider) {
        this.refreshPersistenceAdapter = refreshPersistenceAdapter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponseDto updateRefresh(String refreshToken) {
        boolean isExist = refreshPersistenceAdapter.isExist(refreshToken);
        if (!isExist) {
            throw new ErrorDomain(ErrorCode.REFRESH_INVALID,null);
        }

        try {
            List<String> roles = new ArrayList<>();
            roles.add(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
            String userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.createAccessToken(userId,roles);

            String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
            refreshPersistenceAdapter.update(refreshToken,newRefreshToken);

            return new TokenResponseDto(newAccessToken, newRefreshToken);

        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.ERROR_ABOUT_JWT_WITHREFRESH,null);
        }
    }
}
