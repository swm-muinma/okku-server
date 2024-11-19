    package kr.okku.server.service;

    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.*;
    import kr.okku.server.dto.controller.pick.PickPlatformResponseDto;
    import kr.okku.server.dto.controller.review.*;
    import kr.okku.server.enums.ReviewStatusEnum;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.Duration;
    import java.time.Instant;
    import java.util.*;
    import java.util.stream.Collectors;
    import java.util.stream.IntStream;

    @Service
    public class ReviewService {

        private final ReviewInsightPersistenceAdapter reviewInsightPersistenceAdapter;
        private final ReviewPersistenceAdapter reviewPersistenceAdapter;
        private final PickPersistenceAdapter pickPersistenceAdapter;

        @Autowired
        public ReviewService(
                             ReviewInsightPersistenceAdapter reviewInsightPersistenceAdapter,
                             ReviewPersistenceAdapter reviewPersistenceAdapter,
                             PickPersistenceAdapter pickPersistenceAdapter) {
            this.reviewInsightPersistenceAdapter = reviewInsightPersistenceAdapter;
            this.reviewPersistenceAdapter = reviewPersistenceAdapter;
            this.pickPersistenceAdapter = pickPersistenceAdapter;
        }

        private ProductReviewDto getReviewsByProduct(String productPk, String platform, PickDomain pick,
                                                     String image, String name, Integer price, String url,Date createdAt) {
            try {
                Optional<ReviewDomain> reviews = reviewPersistenceAdapter.findByProductPkAndPlatform(productPk, platform);
                if (reviews.isEmpty()) {
                    PickPlatformResponseDto platformResponseDto = new PickPlatformResponseDto();
                    platformResponseDto.setName(pick.getPlatform().getName());
                    ReviewsDto reviewsDto = ReviewsDto.builder().reviewStatus(ReviewStatusEnum.ERROR).build();
                    if(createdAt!=null && this.isWithinThreeMinutes(createdAt)){
                        reviewsDto.setReviewStatus(ReviewStatusEnum.PROCESSING);
                    }
                    PickDto pickDto = new PickDto(pick.getId(),pick.getImage(),pick.getPrice(),pick.getName(),pick.getUrl(),platformResponseDto);
                    return ProductReviewDto.builder()
                            .pick(pickDto)
                            .reviews(reviewsDto)
                            .platform(platform)
                            .build();
                }
                return createProductReviewDto(reviews, platform, pick, image, name, price, url);
            }catch (Exception e){
                PickPlatformResponseDto platformResponseDto = new PickPlatformResponseDto();
                platformResponseDto.setName(pick.getPlatform().getName());
                ReviewsDto reviewsDto = ReviewsDto.builder().reviewStatus(ReviewStatusEnum.REVIEW_NOT_EXIST).build();
                if(createdAt!=null && this.isWithinThreeMinutes(createdAt)){
                    reviewsDto.setReviewStatus(ReviewStatusEnum.PROCESSING);
                }
                if(platform.equals("zigzag") && platform.equals("musinsa") && platform.equals("29cm") && platform.equals("wcencept")){
                    reviewsDto.setReviewStatus(ReviewStatusEnum.NOT_SUPPORTED_PLATFORM);
                }
                PickDto pickDto = new PickDto(pick.getId(),pick.getImage(),pick.getPrice(),pick.getName(),pick.getUrl(),platformResponseDto);
                return ProductReviewDto.builder()
                        .pick(pickDto)
                        .reviews(reviewsDto)
                        .platform(platform)
                        .build();
            }
        }

        private static boolean isWithinThreeMinutes(Date date) {
            Instant now = Instant.now(); // 현재 시각
            Instant targetTime = date.toInstant(); // Date를 Instant로 변환

            // now와 targetTime 사이의 차이를 계산
            Duration duration = Duration.between(targetTime, now);

            // 차이가 3분(180초) 이하인지 확인
            return duration.getSeconds() <= 180;
        }
        public ProductReviewDto getReviews(String pickId) {
            Optional<PickDomain> pickOptional = pickPersistenceAdapter.findById(pickId);
            if (pickOptional.isEmpty()) {
                return ProductReviewDto.builder().build();
            }

            PickDomain pick = pickOptional.get();
            Date createdAt = pickPersistenceAdapter.getCreatedAt(pickId).orElse(null);
            ProductReviewDto result = getReviewsByProduct(
                    pick.getPk(),
                    pick.getPlatform().getName(),
                    pick,
                    pick.getImage(),
                    pick.getName(),
                    pick.getPrice(),
                    pick.getUrl(),
                    createdAt
            );
            result.setCanFitting(true);
            if(pick.getFittingPart().equals("others") || pick.getFittingPart()==null){
                result.setCanFitting(false);
            }
            return result;
        }

        public ProductReviewDto createProductReviewDto(Optional<ReviewDomain> optionalReviewDomain, String platform, PickDomain pick, String image, String name, Integer price, String url)
        {
            ReviewInsightDomain insight = ReviewInsightDomain.builder().build();
            ReviewStatusEnum status = ReviewStatusEnum.PROCESSING;
            ReviewDomain review = optionalReviewDomain.orElse(null);
            if(review.getReviews().isEmpty()){
                status = ReviewStatusEnum.REVIEW_NOT_EXIST;
            }
            boolean isInsightEmpty = true;
            if (!optionalReviewDomain.isEmpty()) {
                ReviewDomain reviewDomain = optionalReviewDomain.get();
                if (reviewDomain.getReviews() != null && !reviewDomain.getReviews().isEmpty()) {
                    Optional<ReviewInsightDomain> optionalInsight = reviewInsightPersistenceAdapter.findByProductPkAndPlatform(reviewDomain.getProductKey(), reviewDomain.getPlatform());
                    if(!optionalInsight.isEmpty()){
                        insight = optionalInsight.get();
                        isInsightEmpty = false;
                    }
                }
            }

            if (isInsightEmpty) {
                return createEmptyProductReviewDto(pick, image, name, price, url,status);
            }
            status=ReviewStatusEnum.DONE;

            ReviewsDto reviewsDto =  ReviewsDto.builder().build();
            reviewsDto.setReviewSections(insight, review, platform);
            reviewsDto.setReviewStatus(status);

            PickPlatformResponseDto platformInfo = new PickPlatformResponseDto();
            platformInfo.setName(platform);
            double ratingAvg = review.calculateAverageRating();
            return ProductReviewDto.builder()
                    .pick(new PickDto(
                            pick != null ? pick.getId() : null,
                            image,
                            price,
                            name,
                            url,
                            platformInfo
                    ))
                    .ratingAvg(ratingAvg)
                    .reviews(reviewsDto)
                    .platform(platform)
                    .build();
        }

        private ProductReviewDto createEmptyProductReviewDto(PickDomain pick, String image, String name, Integer price, String url, ReviewStatusEnum status) {
            ReviewsDto reviewsDto = ReviewsDto.builder()
                    .reviewStatus(status)
                    .build();
            return ProductReviewDto.builder()
                    .pick(new PickDto(
                            pick != null ? pick.getId() : null,
                            image,
                            price,
                            name,
                            url,
                            new PickPlatformResponseDto()
                    ))
                    .ratingAvg(0)
                    .reviews(reviewsDto)
                    .platform("")
                    .build();
        }

    }
