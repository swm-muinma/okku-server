package kr.okku.server.domain;

import kr.okku.server.enums.FormEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private String itemPk;
    private String itemPlatform;
    private String responseImage;
    private String responseMessage;
    private String fittingResultId;
    private String callTime;
    private String doneTime;
}
