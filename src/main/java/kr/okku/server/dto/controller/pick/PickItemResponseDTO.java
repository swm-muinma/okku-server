package kr.okku.server.dto.controller.pick;

import lombok.Data;

@Data
public class PickItemResponseDTO {
    private String id;
    private String name;
    private int price;
    private String image;
    private String url;
    private PickPlatformResponseDTO platform;

    // Getters and Setters
}
