package kr.okku.server.dto.controller.review;
import kr.okku.server.domain.ReviewDetailDomain;
import kr.okku.server.domain.ReviewDomain;
import kr.okku.server.domain.ReviewInsightDomain;
import kr.okku.server.domain.ReviewSummaryDomain;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.enums.ReviewStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Builder
public class ReviewsDto extends BasicRequestDto {
    @Builder.Default
    private ReviewStatusEnum reviewStatus = ReviewStatusEnum.PROCESSING;

    @Builder.Default
    private List<ReviewSectionDto> cons = Collections.emptyList();

    @Builder.Default
    private List<ReviewSectionDto> pros = Collections.emptyList();

    @Builder.Default
    private String consSummary = "";

    @Builder.Default
    private String prosSummary = "";

    public void setReviewSections(ReviewInsightDomain insight, ReviewDomain review, String platform) {
        List<ReviewDetailDomain> reviews = review != null ? review.getReviews() : Collections.emptyList();
        List<ReviewSummaryDomain> cautions = insight.getCautions();
        List<ReviewSummaryDomain> pros = insight.getPositives();

        this.cons = mapToReviewSection(cautions, reviews, platform);
        this.pros = mapToReviewSection(pros, reviews, platform);

        this.reviewStatus=review.getReviewStatus();

        this.consSummary =Optional.ofNullable(insight.getConsSummary()).orElse("");
        this.prosSummary =Optional.ofNullable(insight.getProsSummary()).orElse("");

    }

    // Private method to handle the mapping logic for review sections
    private List<ReviewSectionDto> mapToReviewSection(List<ReviewSummaryDomain> insights, List<ReviewDetailDomain> reviews, String platform) {
        return insights.stream()
                .map(insight -> createReviewSectionDTO(insight.getDescription(), insight.getReviewIds(), reviews, platform))
                .collect(Collectors.toList());
    }

    // Private method to create ReviewSectionDto from individual insight
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
}
