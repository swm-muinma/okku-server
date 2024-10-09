package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReviewDto extends BasicRequestDto {
    private PickDto pick;
    private ReviewsDto reviews;
    private double ratingAvg;
    private String platform;
}
