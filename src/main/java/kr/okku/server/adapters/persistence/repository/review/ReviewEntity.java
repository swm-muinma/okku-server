package kr.okku.server.adapters.persistence.repository.review;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "reviews")  // MongoDB collection name
public class ReviewEntity {

    @Id
    private String id;

    @Field("platform")
    private String platform;

    @Field("product_key")
    private String productKey;

    @Field("is_done_scrape_reviews")
    private boolean isDoneScrapeReviews;

    @Field("review_list")
    private List<ReviewDetailEntity> reviews;
}
