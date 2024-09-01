package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewSummaryDomain {
    private String description;
    private List<String> reviewIds;
}
