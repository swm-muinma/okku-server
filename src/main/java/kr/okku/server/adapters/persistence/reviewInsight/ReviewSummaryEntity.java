package kr.okku.server.adapters.persistence.reviewInsight;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class ReviewSummaryEntity {

    private String description;
    @Field("review_ids")
    private String[] reviewIds;

}
