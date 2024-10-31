package kr.okku.server.domain.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Date;

@Data
public class CanFittingLogEntity {
    private Date time;
    private String traceId;

    private String userImage;
    private String message;

    public CanFittingLogEntity(TraceId traceId, String userImage, String message){
        this.time = new Date();
        this.userImage=userImage;
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
