package com.collect.service;

import com.collect.CollectApplication;
import com.collect.dto.UserDTO;
import com.collect.mapper.CodeMapper;
import com.collect.mapper.UserMapper;
import com.collect.po.Code;
import com.collect.po.User;
import com.collect.po.enums.UserIdentity;
import com.collect.vo.AttributeVO;
import com.collect.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = CollectApplication.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CodeMapper codeMapper;

    @Transactional
    @Test
    void testRegister(){
        UserDTO userDTO=new UserDTO("1123","123456",UserIdentity.DELIVER,"dxy");
        assert userService.register(userDTO).getCode()==4000;
        assert userService.register(userDTO).getCode()==4001;
//        Map<String,Object> map=new HashMap<>();
//        map.put("email","1123");
//        userMapper.deleteByMap(map);

    }

    @Transactional
    @Test
    void testLogin(){
        UserDTO userDTO=new UserDTO("1123","123456",UserIdentity.DELIVER,null);
        assert userService.login(userDTO).getCode()==4002;
        userService.register(userDTO);
        UserDTO userDTO2=new UserDTO("1123","123457",UserIdentity.DELIVER,null);
        assert userService.login(userDTO2).getCode()==4003;
        assert userService.login(userDTO).getCode()==4000;
//        Map<String,Object> map=new HashMap<>();
//        map.put("email","1123");
//        userMapper.deleteByMap(map);
    }

    @Transactional
    @Test
    void testCode() {
        assert !userService.codeJudge("abc");
        codeMapper.insert(new Code("abc"));
        assert userService.codeJudge("abc");
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", "abc");
//        codeMapper.deleteByMap(map);

    }

    @Transactional
    @Test
    void testGetIdentityByUserId(){
        UserDTO userDTO=new UserDTO("tzh19850355091@163.com","123456", UserIdentity.DELIVER,null);
        userService.register(userDTO);
        Map<String,Object> map=new HashMap<>();
        map.put("email","tzh19850355091@163.com");
        assert UserIdentity.DELIVER==userService.getIdentityByUserId(
                userMapper.selectByMap(map).get(0).getId());
//        Map<String,Object> map2=new HashMap<>();
//        map2.put("email","tzh19850355091@163.com");
//        userMapper.deleteByMap(map2);
    }


}
