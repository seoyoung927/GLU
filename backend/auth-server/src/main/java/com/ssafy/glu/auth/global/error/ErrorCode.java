package com.ssafy.glu.auth.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 서버 오류
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),

    // 공통 오류
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 요청입니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "권한이 없습니다."),
    NOT_FOUND(404, "NOT_FOUND", "리소스를 찾을 수 없습니다."),
    CONFLICT(409, "CONFLICT", "리소스 충돌이 발생했습니다."),


    //로그인 불가능
    LOGIN_INVALID(400, "LOGIN_INVALID", "아이디 또는 비밀번호가 잘못되었습니다."),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저가 없습니다."),
    REFRESH_TOKEN_EXPIRED(401, "REFRESH_TOKEN_EXPIRED", "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(401, "REFRESH_TOKEN_INVALID", "리프레시 토큰이 유효하지 않습니다."),

    // JWT 관련 오류
    INVALID_TOKEN(422, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN", "토큰이 만료되었습니다."),
    UNSUPPORTED_TOKEN(400, "UNSUPPORTED_TOKEN", "지원되지 않는 토큰 형식입니다.");


    private final int httpStatus;
    private final String code;
    private final String message;
}