package kr.okku.server.adapters.persistence.repository.reviewInsight;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "reviewInsights")
public class ReviewInsightEntity {

    @Id
    private String id;

    private String platform;

    @Field("product_pk")
    private String productPk;

    private ReviewSummaryEntity[] cautions;

    private ReviewSummaryEntity[] positives;

    @Field("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
}