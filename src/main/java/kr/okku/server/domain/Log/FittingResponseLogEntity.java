package kr.okku.server.domain.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.okku.server.dto.adapter.FittingRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class FittingResponseLogEntity {
    private Date time;
    private String traceId;
    private String resultId;
    private String message;

    public FittingResponseLogEntity(TraceId traceId, String resultId, String message){
        this.time = new Date();
        this.resultId=resultId;
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
