package kr.okku.server.dto.controller.fitting;

import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.dto.controller.pick.FittingInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetFittingListResponseDto extends BasicRequestDto {
    private List<FittingResultDto> fittingInfos;
}

