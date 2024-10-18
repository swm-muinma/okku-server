package kr.okku.server.dto.controller.fitting;


import kr.okku.server.enums.FittingStatusEnum;
import lombok.Data;

@Data
public class FittingResultDto {
    private String itemName;
    private String itemImage;
    private String itemPlatform;
    private String pickId;
    private String fittingImage;
    private FittingStatusEnum status;
}
