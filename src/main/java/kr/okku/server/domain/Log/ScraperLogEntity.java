package kr.okku.server.domain.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class ScraperLogEntity {
    private Date time;
    private String traceId;

    private String url;
    private String message;

    public ScraperLogEntity(TraceId traceId, String url, String message){
        this.time = new Date();
        this.url=url;
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
