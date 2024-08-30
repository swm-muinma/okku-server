package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewInsightDomain {
    private String id;
    private String platform;
    private String productPk;
    private ReviewSummaryDomain[] cautions;
    private ReviewSummaryDomain[] positives;
}
