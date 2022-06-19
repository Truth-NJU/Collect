package com.collect.vo.http;

import lombok.Getter;

@Getter
public enum CommentHttpStatus implements HttpStatus {

    COMMENT_ALREADY_EXISTS(8000,"已经打分/评论过了，无法重复打分/评论");

    CommentHttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;

    private final String message;
}
