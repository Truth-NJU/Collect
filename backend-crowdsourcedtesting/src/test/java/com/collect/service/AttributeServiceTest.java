package com.collect.service;

import com.collect.CollectApplication;
import com.collect.dto.AttributeDTO;
import com.collect.dto.UserDTO;
import com.collect.mapper.UserMapper;
import com.collect.po.enums.UserIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest(classes = CollectApplication.class)
public class AttributeServiceTest {

    @Autowired
    AttributeService attributeService;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Transactional
    @Test
    void testInit(){
        UserDTO userVO = new UserDTO("213@qq.com", "123456",  UserIdentity.WORKER,"dxy");
        userService.register(userVO);
        HashMap<String,Object> map=new HashMap<>();
        map.put("email","213@qq.com");
        int userId = userMapper.selectByMap(map).get(0).getId();
        AttributeDTO attributeVO=new AttributeDTO();
        attributeVO.setUserId(userId);
        ArrayList<Integer> arrayList=new ArrayList<>();
        arrayList.add(0);arrayList.add(5);
        attributeVO.setDevice(arrayList);
        arrayList.clear();
        arrayList.add(0);arrayList.add(2);
        attributeVO.setPreferTask(arrayList);
        assert  attributeService.init(attributeVO).getCode()==4000;
    }
}
