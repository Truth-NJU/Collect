package com.collect.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.collect.dto.AttributeDTO;
import com.collect.mapper.AttributeMapper;
import com.collect.po.Attribute;
import com.collect.service.AttributeService;
import com.collect.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttributeServiceImpl implements AttributeService {


    @Autowired
    AttributeMapper attributeMapper;

    @Deprecated
    @Override
    public Attribute ifIn(int userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<Attribute> attributeList = attributeMapper.selectByMap(map);
        if (attributeList.size() == 0)
            return null;
        else
            return attributeList.get(0);
    }

    @Override
    public ResponseVO init(AttributeDTO attributeDTO) {
        UpdateWrapper<Attribute> updateWrapper = new UpdateWrapper<>();
        Map<String, Object> updateByUserId = new HashMap<>();
        updateByUserId.put("userId", attributeDTO.getUserId());
        updateWrapper.allEq(updateByUserId);
        attributeMapper.update(attributeDTO.toPO(), updateWrapper);
        return ResponseVO.succeed();
    }
}
