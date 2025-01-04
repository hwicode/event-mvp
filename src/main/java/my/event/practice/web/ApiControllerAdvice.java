package my.event.practice.web;

import lombok.extern.slf4j.Slf4j;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    private static final String CORE_EXCEPTION_FORMAT = "CoreException : {}";
    private static final String EXCEPTION_FORMAT = "Exception : {}";

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<String> handleCoreException(CoreException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error(CORE_EXCEPTION_FORMAT, e.getMessage(), e);
            case WARN -> log.warn(CORE_EXCEPTION_FORMAT, e.getMessage(), e);
            default -> log.info(CORE_EXCEPTION_FORMAT, e.getMessage(), e);
        }
        return new ResponseEntity<>(e.getErrorType().getMessage(), e.getErrorType().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(EXCEPTION_FORMAT, e.getMessage(), e);
        return new ResponseEntity<>(ErrorType.DEFAULT_ERROR.getMessage(), ErrorType.DEFAULT_ERROR.getStatus());
    }

    // 쿼리 파라미터 없이 HTTP 요청을 보내면 발생
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(Exception e) {
        log.error(EXCEPTION_FORMAT, e.getMessage(), e);
        return new ResponseEntity<>(ErrorType.MISSING_PARAMETER_ERROR.getMessage(), ErrorType.MISSING_PARAMETER_ERROR.getStatus());
    }

}
