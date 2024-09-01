package kr.okku.server.dto.controller.review;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {
    private String name;
    private Integer height;
    private Integer weight;
    private String comment;
    private String image;
}
