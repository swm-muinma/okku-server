package kr.okku.server.dto.controller.fitting;


import lombok.Data;

@Data
public class FittingResultDto {
    private String itemName;
    private String itemImage;
    private String itemPlatform;
    private String pickId;
    private String fittingImage;
    private String status;
}
