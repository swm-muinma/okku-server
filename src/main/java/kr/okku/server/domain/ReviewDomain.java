package kr.okku.server.domain;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewDomain {
    private String id;
    private String rating;
    private String gender;
    private String option;
    private int criterion;
    private int height;
    private int weight;
    private String bottomSize;
    private String content;
    private String imageUrl;
}