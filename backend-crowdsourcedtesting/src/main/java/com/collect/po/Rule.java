package com.collect.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.collect.dto.DTO;
import com.collect.dto.RuleDTO;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("rule")
public class Rule implements PO {

    @TableField("id")
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("hint")
    private String hint; // 规则说明

    @Override
    public DTO toDTO() {
        RuleDTO ruleDTO=new RuleDTO();
        ruleDTO.setName(name);
        ruleDTO.setId(id);
        ruleDTO.setHint(hint);
        return ruleDTO;
    }
}
