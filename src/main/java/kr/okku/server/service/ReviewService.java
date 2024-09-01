package kr.okku.server.service;

import kr.okku.server.adapters.persistence.PickPersistenceAdapter;
import kr.okku.server.adapters.persistence.ReviewInsightPersistenceAdapter;
import kr.okku.server.adapters.persistence.ReviewPersistenceAdapter;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.adapters.persistence.repository.review.ReviewRepository;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightRepository;
import kr.okku.server.adapters.scraper.ScraperAdapter;
import kr.okku.server.domain.*;
import kr.okku.server.dto.controller.review.*;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
            throw new ErrorDomain(ErrorCode.MUST_LOGIN)
        }

        ScrapedDataDomain scrapedData = scraperAdapter.scrape(url);

        return scrapedData;
    }
    // 해당 서비스 내 공통 로직을 처리하는 private 메서드
    private ProductReviewDto getReviewsByProduct(String productPk, String platform, PickDomain pick,
                                                 String image, String name, double price, String url) {
        ReviewInsightDomain insight = reviewInsightPersistenceAdapter.getInsightsByProductPkWithPolling(productPk, platform);
        List<ReviewDomain> reviews = reviewPersistenceAdapter.getReviewsByProductPk(productPk, platform, 100);
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

    private ProductReviewDto toDto(String platform, List<ReviewDomain> reviews, ReviewInsightDomain insight,
                                   PickDomain pickDomain, String image, String name, int price, String url) {
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
                .reviews(new ReviewsDto(cons, pros))
                .build();
    }

    private ReviewSectionDto createReviewSectionDTO(String description, List<String> reviewIds, List<ReviewDomain> reviews, String platform) {
        List<CommentDto> comments = reviewIds.stream()
                .map(reviewId -> reviews.stream()
                        .filter(review -> review.getId().equals(reviewId))
                        .findFirst()
                        .orElseGet(() -> reviewPersistenceAdapter.findById(reviewId, platform)
                                .orElse(new ReviewDomain())))
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
}
