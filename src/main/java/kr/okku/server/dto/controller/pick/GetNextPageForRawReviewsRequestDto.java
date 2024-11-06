package kr.okku.server.dto.controller.pick;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetNextPageForRawReviewsRequestDto {
    private String platform;
    private String pk;
    private Integer page;
    private String data;
    private String traceId;
}
