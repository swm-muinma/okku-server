package kr.okku.server.exception;

import kr.okku.server.exception.ErrorCode;

// ErrorDomain.java
public class ErrorDomain extends RuntimeException {
    private final int statusCode;

    public ErrorDomain(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
