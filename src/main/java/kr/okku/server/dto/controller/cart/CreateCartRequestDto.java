package kr.okku.server.dto.controller.cart;

import lombok.Data;
import java.util.List;

@Data
public class CreateCartRequestDto {
    private String name;
    private List<String> pickIds;
}
