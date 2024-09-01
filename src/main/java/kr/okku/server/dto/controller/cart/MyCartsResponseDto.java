package kr.okku.server.dto.controller.cart;

import kr.okku.server.dto.controller.PageInfoResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class MyCartsResponseDto {

    private List<CartDto> carts;
    private PageInfoResponseDTO page;
    // Getters and Setters
}
