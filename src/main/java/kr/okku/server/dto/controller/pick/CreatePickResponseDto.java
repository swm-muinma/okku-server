package kr.okku.server.dto.controller.pick;

import kr.okku.server.domain.PlatformDomain;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreatePickResponseDto {
    private String id;
    private String url;
    private String userId;
    private String name;
    private int price;
    private String image;

    private List<String> fittingList;
    private PlatformDomain platform;
    private String pk;
    private String brand;
    private String category;
    private String fittingPart;

    private String urlForRawReviews;
    private RequestBodyDto requestBody;
    private Integer page;
    private Boolean lastPage;
    private String traceId;
}
