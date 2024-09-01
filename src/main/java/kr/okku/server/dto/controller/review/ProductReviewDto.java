package kr.okku.server.dto.controller.review;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReviewDto {
    private PickDto pick;
    private ReviewsDto reviews;
}
