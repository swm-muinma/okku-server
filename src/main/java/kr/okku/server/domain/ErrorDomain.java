package kr.okku.server.domain;
public class ErrorDomain extends RuntimeException {

    private final int statusCode;

    public ErrorDomain(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
