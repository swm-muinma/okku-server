package kr.okku.server.dto.controller.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateCartResponseDto {
    private String id;
    private String name;
    private List<String> pickIds;
}
