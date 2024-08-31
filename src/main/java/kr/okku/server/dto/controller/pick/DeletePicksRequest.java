package kr.okku.server.dto.controller.pick;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeletePicksRequest {

    private String userId;
    private List<String> pickIds;
    private String cartId;

    @JsonProperty("isDeletePermenant")
    private boolean isDeletePermenant;

    // Getters and Setters
}
