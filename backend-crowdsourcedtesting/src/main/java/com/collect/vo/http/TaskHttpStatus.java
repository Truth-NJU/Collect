package com.collect.vo.http;

import lombok.Getter;

@Getter
public enum TaskHttpStatus implements HttpStatus {

    TASK_NOT_EXIST(5001, "任务不存在"),
    TASK_DATE_ERROR(5002, "任务截止时间错误"),
    TASK_PARTNUMBER_ERROR(5003, "任务参加人数错误"),
    TASK_NOT_OWN_PRIVILEGE(5004, "用户无任务的操作权限"),

    NO_TASK_FIND_F(5005, "该用户没有发布任务"),

    TASK_Identity_Error(5006, "身份错误，无法查看"),

    NO_TASK_FIND_Z(5007, "该用户没有参与任务"),
    TASK_DELETE_FAIL(5008, "文件删除失败"),
    TASK_FILE_EXT_ERROR(5009, "文件上传格式错误"),
    USER_HAS_TAKEN_TASK(5010, "用户已经参加了该任务"),
    USER_IS_JUST_DELIVER(5011, "用户就是这个包的发包方"),
    TASK_HAS_FINISHED(5012, "任务已经结束无法修改"),
    TASK_HAS_NOT_FINISHED(5013,"任务尚未结束，无法查看结果");

    TaskHttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;

    private final String message;
}
