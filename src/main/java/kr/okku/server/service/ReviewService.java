package kr.okku.server.service;

import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.dto.controller.review.*;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new ErrorDomain(ErrorCode.MUST_LOGIN);
        }

        Optional<ScrapedDataDomain> scrapedData = scraperAdapter.scrape(url);

        return scrapedData.get();
    }
    // 해당 서비스 내 공통 로직을 처리하는 private 메서드
    private ProductReviewDto getReviewsByProduct(String productPk, String platform, PickDomain pick,
                                                 String image, String name, Integer price, String url) {
        ReviewDomain reviews = reviewPersistenceAdapter.findByProductPkAndPlatform(productPk, platform);
        ReviewInsightDomain insight = this.createReviewInsightDomain(reviews);
        return toDto(platform, reviews, insight, pick, image, name, price, url);
    }

    // 로그인 없이 리뷰를 가져오는 메서드
    @Transactional
    public ProductReviewDto getReviewsWithoutLogin(String productPk, String platform, String okkuId) {
        if (okkuIds.contains(okkuId)) {
            throw new ErrorDomain(ErrorCode.MUST_LOGIN);
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

    private ProductReviewDto toDto(String platform, ReviewDomain review, ReviewInsightDomain insight,
                                   PickDomain pickDomain, String image, String name, Integer price, String url) {
        List<ReviewDetailDomain> reviews =review.getReviews();
        // cons와 pros의 ReviewSectionDTO 생성
        List<ReviewSectionDto> cons = insight.getCautions().stream()
                .map(caution -> createReviewSectionDTO(caution.getDescription(), caution.getReviewIds(), reviews, platform))
                .collect(Collectors.toList());

        List<ReviewSectionDto> pros = insight.getPositives().stream()
                .map(positive -> createReviewSectionDTO(positive.getDescription(), positive.getReviewIds(), reviews, platform))
                .collect(Collectors.toList());

        return ProductReviewDto.builder()
                .pick(new PickDto(
                        pickDomain != null ? pickDomain.getId() : null,
                        image,
                        name,
                        price,
                        url
                ))
                .reviews(new ReviewsDto(review.isDoneScrapeReviews(),cons, pros))
                .build();
    }

    private ReviewSectionDto createReviewSectionDTO(String description, List<String> reviewIds, List<ReviewDetailDomain> reviews, String platform) {
        List<CommentDto> comments = reviewIds.stream()
                .flatMap(reviewId -> reviews.stream()
                        .filter(review -> review.getId().equals(reviewId))
                        .findFirst()
                        .stream()) // Optional을 스트림으로 변환하여, 값이 없으면 빈 스트림이 되도록 처리
                .map(review -> new CommentDto(
                        review.getGender() != null ? review.getGender() : "",
                        review.getHeight(),
                        review.getWeight(),
                        review.getContent(),
                        review.getImageUrl() != null ? review.getImageUrl() : ""
                ))
                .collect(Collectors.toList());


        return new ReviewSectionDto(description, comments.size(), comments);
    }

    private ReviewInsightDomain createReviewInsightDomain(ReviewDomain reviewDomain) {
        if (!reviewDomain.isDoneScrapeReviews() && reviewDomain.getReviews() != null && !reviewDomain.getReviews().isEmpty()) {
            return reviewInsightPersistenceAdapter.findByProductPkAndPlatform(reviewDomain.getProductKey(), reviewDomain.getPlatform());
        }

        if (!reviewDomain.isDoneScrapeReviews() && (reviewDomain.getReviews() == null || reviewDomain.getReviews().isEmpty())) {
            return createEmptyReviewInsightDomain();
        }

        if (reviewDomain.isDoneScrapeReviews() && (reviewDomain.getReviews() == null || reviewDomain.getReviews().isEmpty())) {
            return createEmptyReviewInsightDomain();
        }

        return createEmptyReviewInsightDomain();
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
