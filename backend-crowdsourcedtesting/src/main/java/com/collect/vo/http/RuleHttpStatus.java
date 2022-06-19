package com.collect.vo.http;

import lombok.Getter;

@Getter
public enum RuleHttpStatus implements HttpStatus {

    RULES_ZERO(8001, "没有任何策略"),
    RULE_NOT_IN(8002, "不是一个策略"),
    RULE_MASK_ERROR(8003, "属性掩码错误");

    RuleHttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;

    private final String message;


}
