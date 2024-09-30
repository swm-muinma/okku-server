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
        Sentry.withScope(scope -> {
            if (ex.getRequestDto() != null) {
                scope.setExtra("Request Data", ex.getRequestDto().toString());
            }
            Sentry.captureException(ex);
        });
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Sentry.captureException(ex);
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
