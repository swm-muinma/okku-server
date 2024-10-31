package kr.okku.server.domain.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.okku.server.dto.adapter.ScraperResponseDto;
import lombok.Data;

import java.util.Date;

@Data
public class ScraperReponseLogEntity {
    private Date time;
    private String traceId;
    private ScraperResponseDto scraperResponseDto;
    private String message;

    public ScraperReponseLogEntity(TraceId traceId, ScraperResponseDto scraperResponseDto, String message){
        this.time = new Date();
        this.scraperResponseDto=scraperResponseDto;
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
