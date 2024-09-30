package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

@Data
public class NewPickRequestDto extends BasicRequestDto {

    private String url;

}
