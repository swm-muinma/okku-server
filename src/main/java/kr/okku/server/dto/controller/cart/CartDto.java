package kr.okku.server.dto.controller.cart;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartDto {

    private String id;
    private String name;
    private Integer picksNum;
    private List<String> picksImages;
    // Getters and Setters
}
