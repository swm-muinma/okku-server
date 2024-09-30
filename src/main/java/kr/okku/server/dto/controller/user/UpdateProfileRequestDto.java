package kr.okku.server.dto.controller.user;

import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.enums.FormEnum;
import lombok.Data;

@Data
public class UpdateProfileRequestDto extends BasicRequestDto {
    private String name;
    private Integer height;
    private Integer weight;
    private FormEnum form;
}