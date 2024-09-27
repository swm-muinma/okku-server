package kr.okku.server.dto.controller.pick;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PickFittingResponseDto {
    private String id;
    private String name;
    private int price;
    private String image;
    private String url;
    private PickPlatformResponseDTO platform;
    private String fittingImage;
}
