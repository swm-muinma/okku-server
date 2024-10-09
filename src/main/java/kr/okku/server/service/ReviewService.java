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

        private final ScraperAdapter scraperAdapter;
        private final ReviewInsightPersistenceAdapter reviewInsightPersistenceAdapter;
        private final ReviewPersistenceAdapter reviewPersistenceAdapter;
        private final PickPersistenceAdapter pickPersistenceAdapter;
        private final List<String> okkuIds = new ArrayList<>();

        @Autowired
        public ReviewService(ScraperAdapter scraperAdapter,
                             ReviewInsightPersistenceAdapter reviewInsightPersistenceAdapter,
                             ReviewPersistenceAdapter reviewPersistenceAdapter,
                             PickPersistenceAdapter pickPersistenceAdapter) {
            this.scraperAdapter = scraperAdapter;
            this.reviewInsightPersistenceAdapter = reviewInsightPersistenceAdapter;
            this.reviewPersistenceAdapter = reviewPersistenceAdapter;
            this.pickPersistenceAdapter = pickPersistenceAdapter;
        }

        public ScrapedDataDomain getItemInfoWithoutLogin(String url, String okkuId) {
            if (okkuIds.contains(okkuId)) {
                throw new ErrorDomain(ErrorCode.MUST_LOGIN,null);
            }

            Optional<ScrapedDataDomain> scrapedData = scraperAdapter.scrape(url);

            return scrapedData.get();
        }
        private ProductReviewDto getReviewsByProduct(String productPk, String platform, PickDomain pick,
                                                     String image, String name, Integer price, String url) {
            Optional<ReviewDomain> reviews = reviewPersistenceAdapter.findByProductPkAndPlatform(productPk, platform);
            return createProductReviewDto(reviews, platform, pick, image, name, price, url);
        }

        @Transactional
        public ProductReviewDto getReviewsWithoutLogin(String productPk, String platform, String okkuId) {
            if (okkuIds.contains(okkuId)) {
                throw new ErrorDomain(ErrorCode.MUST_LOGIN,null);
            }

            okkuIds.add(okkuId);
            return getReviewsByProduct(productPk, platform, null, "", "", 0, "url");
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
        
        private ReviewSectionDto createReviewSectionDTO(String description, List<String> reviewIds, List<ReviewDetailDomain> reviews, String platform) {
            List<CommentDto> comments = IntStream.range(0, reviewIds.size())
                    .mapToObj(index -> {
                        try {
                            if (index < reviews.size()) {
                                ReviewDetailDomain review = reviews.get(Integer.parseInt(reviewIds.get(index)));
                                return new CommentDto(
                                        review.getGender() != null ? review.getGender() : "",
                                        review.getHeight(),
                                        review.getWeight(),
                                        review.getContent(),
                                        review.getImageUrl() != null ? review.getImageUrl() : new ArrayList<>(),
                                        review.getRating()
                                );
                            }
                        } catch (IndexOutOfBoundsException | NumberFormatException e) {
                            return null;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return new ReviewSectionDto(description, comments.size(), comments);
        }

        public ProductReviewDto createProductReviewDto(Optional<ReviewDomain> optionalReviewDomain, String platform, PickDomain pick, String image, String name, Integer price, String url) {
            ReviewInsightDomain insight;
            boolean isInsightEmpty = true;
            if (optionalReviewDomain.isEmpty()) {
                insight = createEmptyReviewInsightDomain();
            } else {
                ReviewDomain reviewDomain = optionalReviewDomain.get();
                if (reviewDomain.getReviews() != null && !reviewDomain.getReviews().isEmpty()) {
                    Optional<ReviewInsightDomain> optionalInsight = reviewInsightPersistenceAdapter.findByProductPkAndPlatform(reviewDomain.getProductKey(), reviewDomain.getPlatform());

                    if(optionalInsight.isEmpty()){
                        insight = createEmptyReviewInsightDomain();
                    }else{
                        insight = optionalInsight.get();
                        isInsightEmpty = false;
                    }
                } else {
                    insight = createEmptyReviewInsightDomain();
                }
            }

            if (isInsightEmpty) {
                return createEmptyProductReviewDto(pick, image, name, price, url);
            }

            ReviewDomain review = optionalReviewDomain.orElse(null);
            List<ReviewDetailDomain> reviews = review != null ? review.getReviews() : Collections.emptyList();

            List<ReviewSectionDto> cons = insight.getCautions().stream()
                    .map(caution -> createReviewSectionDTO(caution.getDescription(), caution.getReviewIds(), reviews, platform))
                    .collect(Collectors.toList());

            List<ReviewSectionDto> pros = insight.getPositives().stream()
                    .map(positive -> createReviewSectionDTO(positive.getDescription(), positive.getReviewIds(), reviews, platform))
                    .collect(Collectors.toList());

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
                    .reviews(new ReviewsDto(getReviewStatus(review), cons, pros, Optional.ofNullable(insight.getConsSummary()).orElse(""),Optional.ofNullable(insight.getProsSummary()).orElse("")))
                    .platform(platform)
                    .build();
        }

        private ProductReviewDto createEmptyProductReviewDto(PickDomain pick, String image, String name, Integer price, String url) {
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
                    .reviews(new ReviewsDto(ReviewStatusEnum.PROCESSING, Collections.emptyList(), Collections.emptyList(),"",""))
                    .platform("")
                    .build();
        }

        private ReviewStatusEnum getReviewStatus(ReviewDomain review){
            if(review.getReviews() != null && review.getReviews().size()!=0  && review.isDoneScrapeReviews()){
                return ReviewStatusEnum.DONE;
            }
            if(review.isDoneScrapeReviews()){
                return ReviewStatusEnum.REVIEW_NOT_EXIST;
            }
            return ReviewStatusEnum.PROCESSING;
        }

        private ReviewInsightDomain createEmptyReviewInsightDomain() {
            return ReviewInsightDomain.builder()
                    .id("")
                    .platform("")
                    .productPk("")
                    .cautions(Collections.emptyList())
                    .positives(Collections.emptyList())
                    .build();
        }

    }
