package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class ReviewInsightDomain {
    private String id;

    @Builder.Default
    private String platform = "";

    @Builder.Default
    private String productPk = "";

    @Builder.Default
    private List<ReviewSummaryDomain> cautions = Collections.emptyList();

    @Builder.Default
    private List<ReviewSummaryDomain> positives = Collections.emptyList();

    @Builder.Default
    private String consSummary = "";

    @Builder.Default
    private String prosSummary = "";

    @Builder.Default
    private Integer reviewLen = 0;
}

