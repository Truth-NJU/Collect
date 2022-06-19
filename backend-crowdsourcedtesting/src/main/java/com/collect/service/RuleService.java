package com.collect.service;

import com.collect.vo.ResponseVO;
import com.collect.vo.RuleVO;

import java.util.List;

public interface RuleService {
    ResponseVO getRules();

    ResponseVO setRules(List<RuleVO> ids);
}
