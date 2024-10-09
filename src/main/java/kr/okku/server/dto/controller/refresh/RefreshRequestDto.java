package kr.okku.server.dto.controller.refresh;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDto extends BasicRequestDto {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
