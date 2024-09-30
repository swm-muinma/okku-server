package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto extends BasicRequestDto {
    private String name;
    private Integer height;
    private Integer weight;
    private String comment;
    private String image;
}
