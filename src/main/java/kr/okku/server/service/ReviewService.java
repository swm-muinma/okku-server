    package kr.okku.server.service;

    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.*;
    import kr.okku.server.dto.controller.pick.PickPlatformResponseDto;
    import kr.okku.server.dto.controller.review.*;
    import kr.okku.server.enums.ReviewStatusEnum;
    import kr.okku.server.exception.ErrorCode;
    import kr.okku.server.exception.ErrorDomain;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

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
                                                     String image, String name, Integer price, String url) {
            try {
                Optional<ReviewDomain> reviews = reviewPersistenceAdapter.findByProductPkAndPlatform(productPk, platform);
                if (reviews.isEmpty()) {
                    PickPlatformResponseDto platformResponseDto = new PickPlatformResponseDto();
                    platformResponseDto.setName(pick.getName());
                    ReviewsDto reviewsDto = ReviewsDto.builder().reviewStatus(ReviewStatusEnum.ERROR).build();
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
                platformResponseDto.setName(pick.getName());
                ReviewsDto reviewsDto = ReviewsDto.builder().reviewStatus(ReviewStatusEnum.ERROR).build();
                PickDto pickDto = new PickDto(pick.getId(),pick.getImage(),pick.getPrice(),pick.getName(),pick.getUrl(),platformResponseDto);
                return ProductReviewDto.builder()
                        .pick(pickDto)
                        .reviews(reviewsDto)
                        .platform(platform)
                        .build();
            }
        }

        public ProductReviewDto getReviews(String pickId) {
            Optional<PickDomain> pickOptional = pickPersistenceAdapter.findById(pickId);
            if (pickOptional.isEmpty()) {
                return ProductReviewDto.builder().build();
            }

            PickDomain pick = pickOptional.get();
            return getReviewsByProduct(
                    pick.getPk(),
                    pick.getPlatform().getName(),
                    pick,
                    pick.getImage(),
                    pick.getName(),
                    pick.getPrice(),
                    pick.getUrl()
            );
        }

        public ProductReviewDto createProductReviewDto(Optional<ReviewDomain> optionalReviewDomain, String platform, PickDomain pick, String image, String name, Integer price, String url) {
            ReviewInsightDomain insight = ReviewInsightDomain.builder().build();
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
                return createEmptyProductReviewDto(pick, image, name, price, url);
            }

            ReviewDomain review = optionalReviewDomain.orElse(null);

            ReviewsDto reviewsDto =  ReviewsDto.builder().build();
            reviewsDto.setReviewSections(insight, review, platform);


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

        private ProductReviewDto createEmptyProductReviewDto(PickDomain pick, String image, String name, Integer price, String url) {
            ReviewsDto reviewsDto = ReviewsDto.builder().build();
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
