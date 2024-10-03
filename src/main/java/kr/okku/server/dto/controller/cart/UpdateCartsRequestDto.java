package kr.okku.server.dto.controller.cart;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCartsRequestDto extends BasicRequestDto {
    private List<UpdateCartRequestDto> carts;
}
