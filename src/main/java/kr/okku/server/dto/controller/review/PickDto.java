package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.pick.PickPlatformResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PickDto  extends BasicRequestDto {
    private String id;
    private String name;
    private int price;
    private String image;
    private String url;
    private PickPlatformResponseDto platform;
}
