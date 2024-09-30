package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

@Data
public class PickCartResponseDto extends BasicRequestDto {
    private String name;
    private PickCartHostResponseDto host;

    // Getters and Setters
}
