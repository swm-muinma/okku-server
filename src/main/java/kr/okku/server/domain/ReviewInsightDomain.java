package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewInsightDomain {
    private String id;
    private String platform;
    private String productPk;
    private List<ReviewSummaryDomain> cautions;
    private List<ReviewSummaryDomain> positives;
    private String consSummary;
    private String prosSummary;
    private Integer reviewLen;
}
