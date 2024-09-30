package kr.okku.server.dto.controller.pick;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.PageInfoResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class UserPicksResponseDto extends BasicRequestDto {
    private PickCartResponseDto cart;
    private List<PickItemResponseDto> picks;
    private PageInfoResponseDto page;

    // Getters and Setters
}
