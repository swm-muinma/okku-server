package kr.okku.server.mapper;

import kr.okku.server.adapters.persistence.repository.review.ReviewDetailEntity;
import kr.okku.server.adapters.persistence.repository.review.ReviewEntity;
import kr.okku.server.domain.ReviewDetailDomain;
import kr.okku.server.domain.ReviewDomain;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewMapper {

    // Convert ReviewEntity to ReviewDomain
    public static ReviewDomain toDomain(ReviewEntity reviewEntity) {
        if (reviewEntity == null) {
            return null;
        }
        return ReviewDomain.builder()
                .id(reviewEntity.getId())
                .platform(reviewEntity.getPlatform())
                .productKey(reviewEntity.getProductKey())
                .isDoneScrapeReviews(reviewEntity.isDoneScrapeReviews())
                .reviews(toDetailDomainList(reviewEntity.getReviews()))
                .build();
    }

    // Convert ReviewDomain to ReviewEntity
    public static ReviewEntity toEntity(ReviewDomain reviewDomain) {
        if (reviewDomain == null) {
            return null;
        }
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setId(reviewDomain.getId());
        reviewEntity.setPlatform(reviewDomain.getPlatform());
        reviewEntity.setProductKey(reviewDomain.getProductKey());
        reviewEntity.setDoneScrapeReviews(reviewDomain.isDoneScrapeReviews());
        reviewEntity.setReviews(toDetailEntityList(reviewDomain.getReviews()));
        return reviewEntity;
    }

    // Convert List of ReviewDetailEntity to List of ReviewDetailDomain
    private static List<ReviewDetailDomain> toDetailDomainList(List<ReviewDetailEntity> reviewDetailEntities) {
        if (reviewDetailEntities == null) {
            return null;
        }
        return reviewDetailEntities.stream()
                .map(ReviewMapper::toDetailDomain)
                .collect(Collectors.toList());
    }

    // Convert List of ReviewDetailDomain to List of ReviewDetailEntity
    private static List<ReviewDetailEntity> toDetailEntityList(List<ReviewDetailDomain> reviewDetailDomains) {
        if (reviewDetailDomains == null) {
            return null;
        }
        return reviewDetailDomains.stream()
                .map(ReviewMapper::toDetailEntity)
                .collect(Collectors.toList());
    }

    // Convert ReviewDetailEntity to ReviewDetailDomain
    private static ReviewDetailDomain toDetailDomain(ReviewDetailEntity reviewDetailEntity) {
        if (reviewDetailEntity == null) {
            return null;
        }
        return ReviewDetailDomain.builder()
                .rating(reviewDetailEntity.getRating())
                .gender(reviewDetailEntity.getGender())
                .option(reviewDetailEntity.getOption())
                .criterion(reviewDetailEntity.getCriterion())
                .height(reviewDetailEntity.getHeight())
                .weight(reviewDetailEntity.getWeight())
                .topSize(reviewDetailEntity.getTopSize())
                .bottomSize(reviewDetailEntity.getBottomSize())
                .content(reviewDetailEntity.getContent())
                .imageUrl(reviewDetailEntity.getImageUrls())
                .build();
    }

    // Convert ReviewDetailDomain to ReviewDetailEntity
    private static ReviewDetailEntity toDetailEntity(ReviewDetailDomain reviewDetailDomain) {
        if (reviewDetailDomain == null) {
            return null;
        }
        ReviewDetailEntity reviewDetailEntity = new ReviewDetailEntity();
        reviewDetailEntity.setRating(reviewDetailDomain.getRating());
        reviewDetailEntity.setGender(reviewDetailDomain.getGender());
        reviewDetailEntity.setOption(reviewDetailDomain.getOption());
        reviewDetailEntity.setCriterion(reviewDetailDomain.getCriterion());
        reviewDetailEntity.setHeight(reviewDetailDomain.getHeight());
        reviewDetailEntity.setWeight(reviewDetailDomain.getWeight());
        reviewDetailEntity.setTopSize(reviewDetailDomain.getTopSize());
        reviewDetailEntity.setBottomSize(reviewDetailDomain.getBottomSize());
        reviewDetailEntity.setContent(reviewDetailDomain.getContent());
        reviewDetailEntity.setImageUrls(reviewDetailDomain.getImageUrl());
        return reviewDetailEntity;
    }
}
