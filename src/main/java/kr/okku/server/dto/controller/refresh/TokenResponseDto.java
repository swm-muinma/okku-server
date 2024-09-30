package kr.okku.server.dto.controller.refresh;

import kr.okku.server.dto.controller.BasicRequestDto;

public class TokenResponseDto extends BasicRequestDto {
    private String accessToken;
    private String refreshToken;

    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}