package com.collect.service;

import com.collect.dto.AttributeDTO;
import com.collect.po.Attribute;
import com.collect.vo.ResponseVO;


public interface AttributeService {
    Attribute ifIn(int userId);
    ResponseVO init(AttributeDTO attributeDTO);
}
