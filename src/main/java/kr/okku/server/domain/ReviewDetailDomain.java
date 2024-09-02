package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDetailDomain {
    private String id;
    private String rating;
    private String gender;
    private String option;
    private String criterion;
    private Integer height;
    private Integer weight;
    private String topSize;
    private String bottomSize;
    private String content;
    private String imageUrl;
}
