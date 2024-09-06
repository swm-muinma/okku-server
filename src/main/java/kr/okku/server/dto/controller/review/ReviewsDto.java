package kr.okku.server.dto.controller.review;
import kr.okku.server.enums.ReviewStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewsDto {
    private ReviewStatusEnum reviewStatus;
    private List<ReviewSectionDto> cons;
    private List<ReviewSectionDto> pros;
}
