    package kr.okku.server.service;

    import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
    import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
    import kr.okku.server.adapters.scraper.ScraperAdapter;
    import kr.okku.server.domain.*;
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
        // 해당 서비스 내 공통 로직을 처리하는 private 메서드
        private ProductReviewDto getReviewsByProduct(String productPk, String platform, PickDomain pick,
                                                     String image, String name, Integer price, String url) {
            Optional<ReviewDomain> reviews = reviewPersistenceAdapter.findByProductPkAndPlatform(productPk, platform);
            return createProductReviewDto(reviews, platform, pick, image, name, price, url);
        }

        // 로그인 없이 리뷰를 가져오는 메서드
        @Transactional
        public ProductReviewDto getReviewsWithoutLogin(String productPk, String platform, String okkuId) {
            if (okkuIds.contains(okkuId)) {
                throw new ErrorDomain(ErrorCode.MUST_LOGIN,null);
            }

            // 로그인 없이 사용할 때는 pick과 관련된 데이터는 없으므로 null을 전달
            okkuIds.add(okkuId);
            return getReviewsByProduct(productPk, platform, null, "", "", 0, "url");
        }

        // 로그인 후 리뷰를 가져오는 메서드
        public ProductReviewDto getReviews(String pickId) {
            Optional<PickDomain> pickOptional = pickPersistenceAdapter.findById(pickId);
            if (pickOptional.isEmpty()) {
                return ProductReviewDto.builder().build();
            }

            PickDomain pick = pickOptional.get();
            // pick 객체의 정보로 DTO 생성
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
            // ReviewInsightDomain 생성 로직
            ReviewInsightDomain insight;
            boolean isInsightEmpty = true;
            if (optionalReviewDomain.isEmpty()) {
                // Optional이 비어있을 경우 빈 ReviewInsightDomain 생성
                insight = createEmptyReviewInsightDomain();
            } else {
                ReviewDomain reviewDomain = optionalReviewDomain.get();
                if (reviewDomain.getReviews() != null && !reviewDomain.getReviews().isEmpty()) {
                    // 정상적으로 리뷰가 완료되었고 리뷰 목록이 존재할 경우
                    Optional<ReviewInsightDomain> optionalInsight = reviewInsightPersistenceAdapter.findByProductPkAndPlatform(reviewDomain.getProductKey(), reviewDomain.getPlatform());


                    if(optionalInsight.isEmpty()){
                        insight = createEmptyReviewInsightDomain();
                    }else{
                        insight = optionalInsight.get();
                        isInsightEmpty = false;
                    }
                } else {
                    // 리뷰가 완료되지 않았거나 리뷰 목록이 없을 경우 빈 ReviewInsightDomain 생성
                    insight = createEmptyReviewInsightDomain();
                }
            }
            // 만약 insight가 비어있는 경우라면, toDto 호출 없이 빈 ProductReviewDto를 반환할 수도 있음
            if (isInsightEmpty) {
                return createEmptyProductReviewDto(pick, image, name, price, url);
            }

            // toDto 로직 통합
            ReviewDomain review = optionalReviewDomain.orElse(null);
            List<ReviewDetailDomain> reviews = review != null ? review.getReviews() : Collections.emptyList();

            // cons와 pros의 ReviewSectionDTO 생성
            List<ReviewSectionDto> cons = insight.getCautions().stream()
                    .map(caution -> createReviewSectionDTO(caution.getDescription(), caution.getReviewIds(), reviews, platform))
                    .collect(Collectors.toList());

            List<ReviewSectionDto> pros = insight.getPositives().stream()
                    .map(positive -> createReviewSectionDTO(positive.getDescription(), positive.getReviewIds(), reviews, platform))
                    .collect(Collectors.toList());

            // ProductReviewDto 생성
            return ProductReviewDto.builder()
                    .pick(new PickDto(
                            pick != null ? pick.getId() : null,
                            image,
                            name,
                            price,
                            url
                    ))
                    .reviews(new ReviewsDto(getReviewStatus(review), cons, pros))
                    .build();
        }


        // 만약 빈 ProductReviewDto 생성 로직이 필요하다면 이 메서드를 이용할 수 있음
        private ProductReviewDto createEmptyProductReviewDto(PickDomain pick, String image, String name, Integer price, String url) {
            return ProductReviewDto.builder()
                    .pick(new PickDto(
                            pick != null ? pick.getId() : null,
                            image,
                            name,
                            price,
                            url
                    ))
                    .reviews(new ReviewsDto(ReviewStatusEnum.PROCESSING, Collections.emptyList(), Collections.emptyList()))
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
