package kr.okku.server.dto.controller.pick;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitRawReviewsResponseDto {
    private String platform;
    private String pk;
    private String traceId;
    private String status;
}
