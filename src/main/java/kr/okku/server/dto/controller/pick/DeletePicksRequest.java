package kr.okku.server.dto.controller.pick;

import lombok.Data;

import java.util.List;

@Data
public class DeletePicksRequest {

    private String userId;
    private List<String> pickIds;
    private String cartId;
    private boolean deletePermenant;

    // Getters and Setters
}
