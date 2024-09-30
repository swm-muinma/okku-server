package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

@Data
public class GetMyPickRequestDto extends BasicRequestDto {

    private String cartId;
    private int page;
    private int size;

}
