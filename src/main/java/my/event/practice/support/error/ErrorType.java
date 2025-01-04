package my.event.practice.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 예기치 못한 에러가 발생했습니다.",
            LogLevel.ERROR),

    MISSING_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "회원 아이디 없이 로그인 요청을 보냈습니다",
                  LogLevel.INFO);

    private final HttpStatus status;

    private final String message;

    private final LogLevel logLevel;

    ErrorType(HttpStatus status, String message, LogLevel logLevel) {
        this.status = status;
        this.message = message;
        this.logLevel = logLevel;
    }
}
