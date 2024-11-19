package kr.okku.server.dto.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@Data
@AllArgsConstructor
public class FittingRequestDto {
    private String user_pk;
    private String clothes_class;
    private String fcm_token;
    private String clothes_pk;
    private String clothes_platform;
    private String human_img_url;
    private String clothes_img_url;
    private String pick_id;

}
