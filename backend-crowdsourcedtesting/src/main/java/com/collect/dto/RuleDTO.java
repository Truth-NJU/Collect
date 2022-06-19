package com.collect.dto;

import com.collect.po.PO;
import com.collect.vo.RuleVO;
import com.collect.vo.VO;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RuleDTO implements DTO {

    Integer id;
    String name;
    String hint; // 规则说明

    @Override
    public PO toPO() {
        return null;
    }

    @Override
    public VO toVO() {
        RuleVO ruleVO=new RuleVO();
        ruleVO.setHint(hint);
        ruleVO.setName(name);
        ruleVO.setId(id);
        return ruleVO;
    }
}
