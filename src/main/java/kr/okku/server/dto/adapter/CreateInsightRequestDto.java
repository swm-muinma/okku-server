package kr.okku.server.dto.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateInsightRequestDto {
    private String trace_id;
    private String clothes_pk;
    private String clothes_platform;
}
