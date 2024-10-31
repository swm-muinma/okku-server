package kr.okku.server.exception;

import kr.okku.server.domain.Log.TraceId;
import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.exception.ErrorCode;

// ErrorDomain.java
public class ErrorDomain extends RuntimeException {
    private final int statusCode;
    private String traceId;
    public ErrorDomain(ErrorCode errorCode, TraceId traceId) {
        super(errorCode.getMessage());
        this.traceId = traceId.getId();
        this.statusCode = errorCode.getStatusCode();
    }
    public String getTraceId() {
        return traceId;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
