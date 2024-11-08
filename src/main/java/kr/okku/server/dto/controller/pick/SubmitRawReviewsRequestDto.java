package kr.okku.server.dto.controller.pick;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubmitRawReviewsRequestDto {
    private String platform;
    private String pk;
    private String traceId;
    private List<String> data;
}
