package com.collect.vo.http;

import lombok.Getter;

@Getter
public enum BaseHttpStatus implements HttpStatus {
    COMMON_OK(4000, "ok"),
    FILE_PATH_ERROR(6001, "文件路径错误"),
    FILE_CANNOT_LOAD(6002, "文件读取失败"),
    FILE_CANNOT_SAVE(6003, "文件写入失败");

    BaseHttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;

    private final String message;
}
