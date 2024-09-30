package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PickDto  extends BasicRequestDto {
    private String id;
    private String image;
    private String name;
    private Integer price;
    private String url;
}
