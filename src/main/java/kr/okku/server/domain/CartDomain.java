package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CartDomain {
    private String id;
    private String userId;
    private String name;
    private int pickNum;

    private int orderIndex;
    private List<String> pickItemIds;

    public void deletePicks(List<String> pickIds){
        List<String> modifiablePickItemIds = new ArrayList<>(this.pickItemIds);
        modifiablePickItemIds.removeAll(pickIds);
        this.pickItemIds = modifiablePickItemIds;
        this.pickNum = modifiablePickItemIds.size();
    }

    public void addPicks(List<String> pickIds){
        List<String> modifiablePickItemIds = new ArrayList<>(this.pickItemIds);
        List<String> pickIdForAdd = pickIds.stream()
                .filter(pickId -> !modifiablePickItemIds.contains(pickId))
                .toList();
        modifiablePickItemIds.addAll(pickIdForAdd);
        this.pickItemIds = modifiablePickItemIds;
        this.pickNum = modifiablePickItemIds.size();
    }
}