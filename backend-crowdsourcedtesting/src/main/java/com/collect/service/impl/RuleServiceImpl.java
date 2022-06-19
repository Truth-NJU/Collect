package com.collect.service.impl;


import com.collect.mapper.TaskMapperWrapper;
import com.collect.service.RuleService;
import com.collect.vo.ResponseVO;
import com.collect.vo.RuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleServiceImpl implements RuleService {

//    @Autowired
//    RuleMapper ruleMapper;

    @Autowired
    TaskMapperWrapper taskMapperWrapper;

    @Override
    public ResponseVO getRules() {
//        List<Rule> rules = ruleMapper.selectByMap(null);
//        if (rules.size() == 0)
//            return ResponseVO.fail(RuleHttpStatus.RULES_ZERO);
//        List<VO> t = new ArrayList<>();
//        for (Rule rule : rules) {
//            t.add(rule.toDTO().toVO());
//        }
//        return ResponseVO.succeed(t);
        return ResponseVO.succeed(taskMapperWrapper.findCurrentRule());
    }

    @Override
    public ResponseVO setRules(List<RuleVO> ruleVOS) {
        taskMapperWrapper.changeProcessor(ruleVOS);
        return ResponseVO.succeed(taskMapperWrapper.findCurrentRule());
    }
}
