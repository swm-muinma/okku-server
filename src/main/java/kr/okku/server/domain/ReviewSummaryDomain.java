package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewSummaryDomain {
    private String description;
}
