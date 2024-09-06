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
        // Custom ErrorDomain 예외 처리
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        // 기타 예외 처리 (로그 추가 가능)
        // ex.printStackTrace() 또는 logger 사용 가능
//        Sentry.captureException(ex); // 예외 캡쳐
        ex.printStackTrace(); // 스택 트레
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
