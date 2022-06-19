package com.collect.po.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PreferTask {

    PERFORMANCE(1,"性能测试"),FUNCTION(0,"功能测试"),BUG(2,"稳定性测试");

    @EnumValue
    private final int code;

    @JsonValue
    private final String name;

    PreferTask(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
