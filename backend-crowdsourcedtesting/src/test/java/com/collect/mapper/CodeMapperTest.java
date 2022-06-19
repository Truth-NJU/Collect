package com.collect.mapper;

import com.collect.CollectApplication;
import com.collect.po.Code;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = CollectApplication.class)
public class CodeMapperTest {
    @Autowired
    CodeMapper codeMapper;

    /**
     * 测试插入新的code
     */
    @Test
    void testSelect() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "123");
        assert codeMapper.selectByMap(map).size() == 0;
    }

    /**
     * 测试删除code
     */
    @Test
    void testDelete() {
        codeMapper.insert(new Code("123456"));
        Map<String, Object> map = new HashMap<>();
        map.put("code", "123456");
        assert codeMapper.deleteByMap(map) == 1;
    }

}
