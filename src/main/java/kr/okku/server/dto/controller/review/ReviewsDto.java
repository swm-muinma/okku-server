package kr.okku.server.dto.controller.review;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewsDto {
    private List<ReviewSectionDto> cons;
    private List<ReviewSectionDto> pros;
}
