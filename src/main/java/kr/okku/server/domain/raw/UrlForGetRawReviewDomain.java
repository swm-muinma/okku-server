package kr.okku.server.domain.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlForGetRawReviewDomain {
    private String platform;
    private String version;
    private String url;
}
