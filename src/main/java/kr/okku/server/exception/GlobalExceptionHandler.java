package kr.okku.server.exception;
import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorDomain.class)
    public ResponseEntity<Object> handleErrorDomain(ErrorDomain ex) {
        Sentry.configureScope(scope -> {
            scope.setExtra("TraceId", ex.getTraceId());
        });
        Sentry.captureException(ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Sentry.captureException(ex);
//        ex.printStackTrace();
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
