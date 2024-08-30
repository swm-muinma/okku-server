package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartDomain {
    private String id;
    private String userId;
    private String name;
    private int pickNum;
    private String[] pickItemIds;
}