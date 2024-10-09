package kr.okku.server.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewDetailDomain {
    private String id;
    private Integer rating;
    private String gender;
    private String option;
    private String criterion;
    private String height;
    private String weight;
    private String topSize;
    private String bottomSize;
    private String content;
    private List<String> imageUrl;
}
