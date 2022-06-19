package com.collect.vo;

import com.collect.dto.DTO;
import com.collect.dto.RuleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RuleVO implements VO {
    Integer id;

    String hint; // 规则说明

    String name; // 规则名称

    Boolean valid; // 规则是否启用

    Map<String, Object> extra; // 规则其他属性

    @Override
    public DTO toDTO() {
        return new RuleDTO(null,null,hint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleVO ruleVO = (RuleVO) o;
        return Objects.equals(id, ruleVO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
