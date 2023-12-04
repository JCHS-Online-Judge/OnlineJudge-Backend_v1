package com.github.ioloolo.onlinejudge.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExceptionFactory {

    public static Exception of(Type type) {

        String message = type == null ? "" : type.getMessage();

        return new Exception(message);
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        UNAUTHORIZED("권한이 없습니다."),
        BAD_REQUEST("잘못된 요청입니다."),

        USER_NOT_FOUND("존재하지 않는 사용자입니다."),
        ALREADY_EXIST_USERNAME("이미 가입된 아이디가 존재합니다."),
        ALREADY_EXIST_NAME("이미 가입된 이름이 존재합니다."),

        PROBLEM_NOT_FOUND("존재하지 않는 문제입니다."),
        PROBLEM_NUMBER_ALREADY_EXISTS("이미 존재하는 문제 번호입니다."),
        PROBLEM_TITLE_ALREADY_EXISTS("이미 존재하는 문제 제목입니다."),

        BOARD_NOT_FOUND("존재하지 않는 게시글입니다."),
        FILE_IS_EMPTY("파일이 비어있습니다."),
        FILE_NOT_FOUND("존재하지 않는 파일입니다."),

        LECTURE_NOT_FOUND("존재하지 않는 수업입니다."),

        CONTEST_NOT_FOUND("존재하지 않는 대회입니다."),
        ALREADY_EXIST_CONTEST_TITLE("이미 존재하는 대회 제목입니다."),
        ALREADY_JOINED_CONTEST("이미 참가한 대회입니다."),
        NOT_JOINED_CONTEST("참가하지 않은 대회입니다."),

        HISTORY_NOT_FOUND("존재하지 않는 채점 기록입니다."),

        CONTAINER_NOT_FOUND("채점 컨테이너가 존재하지 않습니다."),
        ;

        private final String message;
    }
}
