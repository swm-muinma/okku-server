package kr.okku.server.dto.controller.review;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewSectionDto {
    private String content;
    private int count;
    private List<CommentDto> comments;
}
