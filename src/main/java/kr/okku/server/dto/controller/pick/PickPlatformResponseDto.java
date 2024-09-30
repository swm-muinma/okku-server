package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

@Data
public class PickPlatformResponseDto extends BasicRequestDto {
    private String name;
    private String image;
    private String url;

    // Getters and Setters
}
