package kr.okku.server.domain;

import kr.okku.server.enums.FormEnum;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class FittingLogDomain {
    private String id;
    private String userId;
    private String userName;
    private String requestUserImage;
    private String requestItemImage;
    private String requestItemUrl;
    private String responseImage;
    private String responseMessage;
    private String fittingResultId;
}
