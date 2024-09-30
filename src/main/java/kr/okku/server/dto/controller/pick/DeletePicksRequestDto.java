package kr.okku.server.dto.controller.pick;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class DeletePicksRequestDto extends BasicRequestDto {

    private List<String> pickIds;
    private String cartId;

    @JsonProperty("isDeletePermenant")
    private boolean isDeletePermenant;

    // Getters and Setters
}
