package kr.okku.server.dto.controller.pick;

import kr.okku.server.domain.PlatformDomain;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetNextPageForRawReviewsResponseDto {
    private String platform;
    private String pk;

    private String urlForRawReviews;
    private RequestBodyDto requestBody;
    private Integer page;
    private Boolean lastPage;
    private String traceId;
}
