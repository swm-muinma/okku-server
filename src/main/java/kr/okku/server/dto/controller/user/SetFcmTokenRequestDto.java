package kr.okku.server.dto.controller.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SetFcmTokenRequestDto {
    private String fcmToken;
}
