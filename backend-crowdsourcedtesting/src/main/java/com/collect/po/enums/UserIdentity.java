package com.collect.po.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.collect.po.ReportImage;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum UserIdentity {
    WORKER(1, "众包工人"), DELIVER(2, "发包方"), ADMINISTRATOR(3, "管理员"),
    ALL(4, "全部");

    UserIdentity(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserIdentity parseFrom(String s) {
        for (UserIdentity identity : UserIdentity.values()) {
            if (identity.name.equals(s)) {
                return identity;
            }
        }
        throw new IllegalArgumentException("No constant with text " + s + " found");
    }

    @EnumValue
    private final int code;

    @JsonValue
    private final String name;
}
