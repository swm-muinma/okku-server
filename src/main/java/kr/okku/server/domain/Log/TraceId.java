package kr.okku.server.domain.Log;
import lombok.Data;

import java.util.UUID;

@Data
public class TraceId {

    private String id;

    public TraceId() {
        this.id = createId();
    }

    private TraceId(String id) {
        this.id = id;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}