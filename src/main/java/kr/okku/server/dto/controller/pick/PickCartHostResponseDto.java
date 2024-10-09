package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PickCartHostResponseDto extends BasicRequestDto {
    private String id;
    private String name;

    // Getters and Setters
}
