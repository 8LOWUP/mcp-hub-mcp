package com.mcphub.global.common.exception.code.status;

import com.mcphub.global.common.exception.code.BaseCodeDto;
import com.mcphub.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements BaseCodeInterface {

    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "AUTH001", "JWT가 없습니다."),
    EXPIRED_MEMBER_JWT(HttpStatus.UNAUTHORIZED, "AUTH002", "만료된 JWT입니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "AUTH003", "지원하지 않는 JWT입니다."),

    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH004", "유효하지 않은 ID TOKEN입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH005", "만료된 REFRESH TOKEN입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH005", "유효하지 않은 ACCESS TOKEN입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH006", "유효하지 않은 REFRESH TOKEN입니다."),
    FAILED_SOCIAL_LOGIN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH007", "소셜 로그인에 실패하였습니다."),

    FAILED_GITHUB_AUTHENTICATION(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH008", "깃허브 서버와 통신이 실패하였습니다."),
    FAILED_GET_APPLE_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH009", "애플 서버와 통신이 실패하였습니다."),
    INVALID_APPLE_ID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH010", "유효하지 않은 애플 ID TOKEN입니다."),

    INVALID_ROLE(HttpStatus.FORBIDDEN, "AUTH011", "권한이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final boolean isSuccess = false;
    private final String code;
    private final String message;

    @Override
    public BaseCodeDto getCode() {
        return BaseCodeDto.builder()
                          .httpStatus(httpStatus)
                          .isSuccess(isSuccess)
                          .code(code)
                          .message(message)
                          .build();
    }
}
