package kr.okku.server.domain.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.okku.server.dto.controller.BasicRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class ControllerLogEntity {
    private Date time;
    private String traceId;
    private String userId;
    private String path;
    private String method;
    private BasicRequestDto request;
    private String message;

    public ControllerLogEntity(TraceId traceId, String userId, String path,String method, BasicRequestDto request,String message){
        this.time = new Date();
        this.traceId=traceId.getId();
        this.userId=userId;
        this.path=path;
        this.method=method;
        this.request=request;
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
