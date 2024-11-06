package kr.okku.server.dto.controller.pick;

import kr.okku.server.domain.PlatformDomain;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@Builder
public class RequestBodyDto {
    private String method;
    private String type;
    private Object data;
}
