package kr.okku.server.dto.controller.pick;

import lombok.Data;

import java.util.List;

@Data
public class MovePicksRequest {

    private List<String> pickIds;
    private String sourceCartId;
    private String destinationCartId;
    private boolean deleteFromOrigin;

    // Getters and Setters
}