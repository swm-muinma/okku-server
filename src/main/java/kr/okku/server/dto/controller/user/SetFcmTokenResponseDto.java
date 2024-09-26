package kr.okku.server.dto.controller.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SetFcmTokenResponseDto {
    private List<String> fcmToken;
}
