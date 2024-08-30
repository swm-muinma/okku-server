package kr.okku.server.mapper;

import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightEntity;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewSummaryEntity;
import kr.okku.server.domain.ReviewInsightDomain;
import kr.okku.server.domain.ReviewSummaryDomain;

import java.util.Arrays;

public class ReviewInsightMapper {

    // ReviewInsightEntity <-> ReviewInsightDomain
    public static ReviewInsightDomain toDomain(ReviewInsightEntity reviewInsightEntity) {
        if (reviewInsightEntity == null) {
            return null;
        }
        return ReviewInsightDomain.builder()
                .id(reviewInsightEntity.getId())
                .platform(reviewInsightEntity.getPlatform())
                .productPk(reviewInsightEntity.getProductPk())
                .cautions(Arrays.stream(toDomain(reviewInsightEntity.getCautions())).toList())
                .positives(Arrays.stream(toDomain(reviewInsightEntity.getPositives())).toList())
                .build();
    }

    public static ReviewInsightEntity toEntity(ReviewInsightDomain reviewInsightDomain) {
        if (reviewInsightDomain == null) {
            return null;
        }
        ReviewInsightEntity reviewInsightEntity = new ReviewInsightEntity();
        reviewInsightEntity.setId(reviewInsightDomain.getId());
        reviewInsightEntity.setPlatform(reviewInsightDomain.getPlatform());
        reviewInsightEntity.setProductPk(reviewInsightDomain.getProductPk());
        reviewInsightEntity.setCautions(toEntity(reviewInsightDomain.getCautions().toArray(new ReviewSummaryDomain[0])));
        reviewInsightEntity.setPositives(toEntity(reviewInsightDomain.getPositives().toArray(new ReviewSummaryDomain[0])));
        return reviewInsightEntity;
    }

    // ReviewSummaryEntity <-> ReviewSummaryDomain
    private static ReviewSummaryDomain[] toDomain(ReviewSummaryEntity[] reviewSummaryEntities) {
        if (reviewSummaryEntities == null) {
            return null;
        }
        ReviewSummaryDomain[] domains = new ReviewSummaryDomain[reviewSummaryEntities.length];
        for (int i = 0; i < reviewSummaryEntities.length; i++) {
            domains[i] = toDomain(reviewSummaryEntities[i]);
        }
        return domains;
    }

    private static ReviewSummaryEntity[] toEntity(ReviewSummaryDomain[] reviewSummaryDomains) {
        if (reviewSummaryDomains == null) {
            return null;
        }
        ReviewSummaryEntity[] entities = new ReviewSummaryEntity[reviewSummaryDomains.length];
        for (int i = 0; i < reviewSummaryDomains.length; i++) {
            entities[i] = toEntity(reviewSummaryDomains[i]);
        }
        return entities;
    }

    private static ReviewSummaryDomain toDomain(ReviewSummaryEntity reviewSummaryEntity) {
        if (reviewSummaryEntity == null) {
            return null;
        }
        return ReviewSummaryDomain.builder()
                .description(reviewSummaryEntity.getDescription())
                .build();
    }

    private static ReviewSummaryEntity toEntity(ReviewSummaryDomain reviewSummaryDomain) {
        if (reviewSummaryDomain == null) {
            return null;
        }
        ReviewSummaryEntity reviewSummaryEntity = new ReviewSummaryEntity();
        reviewSummaryEntity.setDescription(reviewSummaryDomain.getDescription());
        return reviewSummaryEntity;
    }
}
