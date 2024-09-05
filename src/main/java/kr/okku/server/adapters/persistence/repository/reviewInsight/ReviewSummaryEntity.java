package kr.okku.server.adapters.persistence.repository.reviewInsight;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class ReviewSummaryEntity {

    private String description;
    @Field("reviewIds")
    private String[] reviewIds;

}
