package kr.okku.server.dto.controller.user;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SetFcmTokenRequestDto extends BasicRequestDto {
    private String fcmToken;
}
