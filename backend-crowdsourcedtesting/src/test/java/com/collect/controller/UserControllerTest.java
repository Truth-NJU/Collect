package com.collect.controller;

import com.collect.CollectApplication;
import com.collect.mapper.CodeMapper;
import com.collect.mapper.UserMapper;
import com.collect.po.Code;
import com.collect.po.enums.UserIdentity;
import com.collect.service.AttributeService;
import com.collect.service.UserService;
import com.collect.vo.AttributeVO;
import com.collect.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest(classes = CollectApplication.class)
public class UserControllerTest {
    @Autowired
    UserController userController;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CodeMapper codeMapper;

    @Autowired
    UserService userService;

    @Autowired
    AttributeService attributeService;

    @Transactional
    @Test
    void testRegisterDeliver() {
        UserVO userVO = new UserVO("1123", "123456", null, UserIdentity.DELIVER, "dxy");
        assert userController.register(userVO).getCode() == 4004;
        UserVO userVO2 = new UserVO("1123@smail.nju.edu.cn", "123456", null, UserIdentity.DELIVER, "dxy");
        assert userController.register(userVO2).getCode() == 4000;
////        Map<String,Object> map=new HashMap<>();
//        map.put("email","1123@smail.nju.edu.cn");
//        userMapper.deleteByMap(map);
    }

    @Transactional
    @Test
    void testRegisterAdmin() {
        UserVO userVO = new UserVO("213@qq.com", "123456", "abc", UserIdentity.ADMINISTRATOR, "dxy");
        assert userController.register(userVO).getCode() == 4005;
        codeMapper.insert(new Code("abc"));
        assert userController.register(userVO).getCode() == 4000;
//        Map<String, Object> map = new HashMap<>();
//        map.put("email", "213@qq.com");
//        userMapper.deleteByMap(map);
    }

    @Transactional
    @Test
    void testLogin() {
        UserVO userVO = new UserVO("213@qq.com", "123456", "abc", UserIdentity.WORKER, "dxy");
        userController.register(userVO);
        assert userController.login(userVO).getCode() == 4000;
//        Map<String, Object> map = new HashMap<>();
//        map.put("email", "213@qq.com");
//        userMapper.deleteByMap(map);
    }

    @Transactional
    @Test
    void testInit() {
        UserController userController = new UserController();
        userController.userService = userService;
        userController.attributeService = attributeService;

        UserVO userVO = new UserVO("213@qq.com", "123456", "abc", UserIdentity.WORKER, "dxy");
        userController.register(userVO);
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", "213@qq.com");
        int userId = userMapper.selectByMap(map).get(0).getId();
        AttributeVO attributeVO = new AttributeVO();
        attributeVO.setUserId(userId);
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(0);
        arrayList.add(5);
        attributeVO.setDevice(arrayList);
        arrayList.clear();
        arrayList.add(0);
        arrayList.add(2);
        attributeVO.setPreferTask(arrayList);
        assert userController.init(attributeVO).getCode() == 4000;
    }
}