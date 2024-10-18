package kr.okku.server.dto.controller.pick;


import kr.okku.server.enums.FittingStatusEnum;
import lombok.Data;

@Data
public class FittingInfo{
    private String image;
    private FittingStatusEnum status;
}
