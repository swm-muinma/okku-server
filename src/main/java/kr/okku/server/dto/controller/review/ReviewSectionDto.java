package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewSectionDto extends BasicRequestDto {
    private String content;
    private int count;
    private List<CommentDto> comments;
}
