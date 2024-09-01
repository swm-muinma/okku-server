package kr.okku.server.dto.controller.user;

import kr.okku.server.enums.FormEnum;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private Integer height;
    private Integer weight;
    private FormEnum form;
}