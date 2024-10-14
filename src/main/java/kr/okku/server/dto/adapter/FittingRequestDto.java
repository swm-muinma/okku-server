package kr.okku.server.dto.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
public class FittingRequestDto {
    private String user_id;
    private String clothes_class;
    private String fcm_token;
    private String clothes_pk;
    private String clothes_platform;
    private String user_image;

}
