package kr.okku.server.adapters.persistence.repository.review;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class ReviewDetailEntity {

    @Field("rating")
    private Integer rating;

    @Field("gender")
    private String gender;

    @Field("option")
    private String option;

    @Field("criterion")
    private String criterion;

    @Field("height")
    private String height;

    @Field("weight")
    private String weight;

    @Field("top_size")
    private String topSize;

    @Field("bottom_size")
    private String bottomSize;

    @Field("content")
    private String content;

    @Field("age")
    private String age;

    @Field("foot_size")
    private String footSize;

    @Field("image_urls")
    private List<String> imageUrls;
}
