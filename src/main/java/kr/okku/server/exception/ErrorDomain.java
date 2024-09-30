package kr.okku.server.exception;

import kr.okku.server.dto.controller.BasicRequestDto;
import kr.okku.server.exception.ErrorCode;

// ErrorDomain.java
public class ErrorDomain extends RuntimeException {
    private final int statusCode;
    private BasicRequestDto requestDto;
    public ErrorDomain(ErrorCode errorCode, BasicRequestDto requestDto) {
        super(errorCode.getMessage());
        this.requestDto = requestDto;
        this.statusCode = errorCode.getStatusCode();
    }
    public BasicRequestDto getRequestDto() {
        if (requestDto == null) {
            return new BasicRequestDto(); // Return a new instance or modify according to your logic
        }
        return requestDto;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
