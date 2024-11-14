package kr.okku.server.dto.service;

import kr.okku.server.domain.PickDomain;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePickDto {
    PickDomain pickDomain;
    String traceId;
}
