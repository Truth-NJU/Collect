package com.collect.mapper;

import com.collect.CollectApplication;
import com.collect.po.User;
import com.collect.po.enums.UserIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = CollectApplication.class)
public class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    /**
     * 测试根据email查询用户
     */
    @Test
    void testSelect() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", "123");
        assert 0 == userMapper.selectByMap(map).size();
    }

    /**
     * 测试插入新用户
     */
    @Test
    @Transactional
    void testInsert() {
        User user = new User("123", "123", UserIdentity.DELIVER);
        assert 1 == userMapper.insert(user);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "123");
        userMapper.deleteByMap(map);
    }
}
