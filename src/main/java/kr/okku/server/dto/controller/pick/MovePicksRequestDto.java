package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class MovePicksRequestDto extends BasicRequestDto {

    private List<String> pickIds;
    private String sourceCartId;
    private String destinationCartId;
    private boolean deleteFromOrigin;

    // Getters and Setters
}