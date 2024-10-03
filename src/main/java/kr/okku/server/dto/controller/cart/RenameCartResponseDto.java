package kr.okku.server.dto.controller.cart;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RenameCartResponseDto extends BasicRequestDto {
    private String id;
    private String name;
    private List<String> pickIds;
}
