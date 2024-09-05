package kr.okku.server.adapters.persistence.repository.review;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class ReviewDetailEntity {

    @Field("rating")
    private String rating;

    @Field("gender")
    private String gender;

    @Field("option")
    private String option;

    @Field("criterion")
    private String criterion;

    @Field("height")
    private Integer height;

    @Field("weight")
    private Integer weight;

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

    @Field("image_url")
    private String imageUrl;
}
