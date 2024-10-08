package kr.okku.server.dto.controller.cart;

import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.PageInfoResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class MyCartsResponseDto extends BasicRequestDto {

    private List<CartDto> carts;
    private PageInfoResponseDto page;
    // Getters and Setters
}
