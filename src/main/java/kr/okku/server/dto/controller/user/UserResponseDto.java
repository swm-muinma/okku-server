package kr.okku.server.dto.controller.user;

import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.enums.FormEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto extends BasicRequestDto {
    private String id;
    private String name;
    private Integer height;
    private Integer weight;
    private FormEnum form;
}
