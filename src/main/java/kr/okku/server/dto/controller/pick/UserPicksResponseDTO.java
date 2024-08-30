package kr.okku.server.dto.controller.pick;
import kr.okku.server.dto.controller.PageInfoResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserPicksResponseDTO {
    private PickCartResponseDTO cart;
    private List<PickItemResponseDTO> picks;
    private PageInfoResponseDTO page;

    // Getters and Setters
}
