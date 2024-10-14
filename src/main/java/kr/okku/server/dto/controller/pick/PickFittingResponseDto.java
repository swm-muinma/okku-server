package kr.okku.server.dto.controller.pick;


import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PickFittingResponseDto extends BasicRequestDto {
    private String id;
    private String name;
    private int price;
    private String image;
    private String url;
    private PickPlatformResponseDto platform;
    private List<FittingInfo> fittingInfos;

}


