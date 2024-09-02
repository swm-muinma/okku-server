package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewDomain {
    private String id;
    private String platform;
    private String productKey;
    private boolean isDoneScrapeReviews;
    private List<ReviewDetailDomain> reviews;
}
