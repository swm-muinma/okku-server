package kr.okku.server.dto.controller.cart;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;
import java.util.List;

@Data
public class CreateCartRequestDto extends BasicRequestDto {
    private String name;
    private List<String> pickIds;
}
