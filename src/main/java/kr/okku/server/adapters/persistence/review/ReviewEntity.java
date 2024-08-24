package kr.okku.server.adapters.persistence.review;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "reviews")
public class ReviewEntity {

    @Id
    private String id;

    private String rating;
    private String gender;
    private String option;
    private String criterion;
    private String height;
    private String weight;
    @Field("top_size")
    private String topSize;
    @Field("bottom_size")
    private String bottomSize;
    private String content;
    @Field("image_url")
    private String imageUrl;
    @Field("product_pk")
    private String productPk;
    private String platform;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}
