package com.collect.vo.http;

import lombok.Getter;

@Getter
public enum ReportHttpStatus implements HttpStatus {

    Report_NOT_FOUND(7000, "没找到报告"),
    REPORT_UPLOAD_FAIL(7001, "报告文件上传失败"),
    REPORT_WORK_INVALID(7002,"非法的工作状态"),
    REPORT_DUPLICATE_CREATE(7003,"报告重复创建");

    ReportHttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;

    private final String message;
}
