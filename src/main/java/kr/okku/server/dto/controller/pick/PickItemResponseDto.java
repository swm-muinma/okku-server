package kr.okku.server.dto.controller.pick;

import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

@Data
public class PickItemResponseDto extends BasicRequestDto {
    private String id;
    private String name;
    private int price;
    private String image;
    private String url;
    private PickPlatformResponseDto platform;

    // Getters and Setters
}
