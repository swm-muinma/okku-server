package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentDto extends BasicRequestDto {
    private String name;
    private String height;
    private String weight;
    private String comment;
    private List<String> image;
    private Integer rating;
}
