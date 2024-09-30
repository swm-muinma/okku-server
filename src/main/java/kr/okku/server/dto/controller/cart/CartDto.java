package kr.okku.server.dto.controller.cart;


import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartDto extends BasicRequestDto {

    private String id;
    private String name;
    private Integer picksNum;
    private List<String> picksImages;
    // Getters and Setters
}
