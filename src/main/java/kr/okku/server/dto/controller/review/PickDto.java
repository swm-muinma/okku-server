package kr.okku.server.dto.controller.review;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.pick.PickPlatformResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PickDto  extends BasicRequestDto {
    private String id;
    private String image;
    private int price;
    private String name;
    private String url;
    private PickPlatformResponseDto platform;
}
