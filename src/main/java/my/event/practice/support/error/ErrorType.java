package my.event.practice.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    DUPLICATED_MEMBER_ERROR(HttpStatus.BAD_REQUEST, "이미 이벤트에 참여했습니다.",
            LogLevel.INFO),

    QUEUE_CLOSED_ERROR(HttpStatus.BAD_REQUEST, "대기 큐가 이미 닫혔습니다.",
            LogLevel.INFO),

    MEMBER_NOT_IN_QUEUE_ERROR(HttpStatus.NOT_FOUND, "대기 큐에 없는 회원입니다.",
            LogLevel.INFO),

    INVALID_MEMBER_ID_ERROR(HttpStatus.BAD_REQUEST, "회원 아이디가 유효하지 않습니다",
            LogLevel.INFO),

    // 웹 예외
    INVALID_AUTH_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "잘못된 형식의 토큰입니다.",
            LogLevel.INFO),

    EMPTY_AUTHORIZATION_HEADER_ERROR(HttpStatus.BAD_REQUEST, "HTTP AUTHORIZATION 헤더가 비어있습니다.",
            LogLevel.INFO),

    MISSING_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "회원 아이디 없이 로그인 요청을 보냈습니다.",
            LogLevel.INFO),

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 예기치 못한 에러가 발생했습니다.",
            LogLevel.ERROR);

    private final HttpStatus status;

    private final String message;

    private final LogLevel logLevel;

    ErrorType(HttpStatus status, String message, LogLevel logLevel) {
        this.status = status;
        this.message = message;
        this.logLevel = logLevel;
    }
}
