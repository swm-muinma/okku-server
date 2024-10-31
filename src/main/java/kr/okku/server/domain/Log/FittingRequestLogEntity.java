package kr.okku.server.domain.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.okku.server.dto.adapter.FittingRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class FittingRequestLogEntity {
    private Date time;
    private String traceId;

    private FittingRequestDto fittingRequestDto;
    private String message;

    public FittingRequestLogEntity(TraceId traceId, FittingRequestDto fittingRequestDto, String message){
        this.time = new Date();
        this.fittingRequestDto=fittingRequestDto;
        this.traceId=traceId.getId();
        this.message=message;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
