package com.collect.mapper;

import com.collect.po.Report;
import com.collect.po.ReportImage;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.enums.UserIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ReportImageMapperTest {

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    ReportImageMapper reportImageMapper;

    /**
     * 测试删除报告下图片
     */
    @Test
    public void testDelete() {
        User user0 = new User("123", "321", UserIdentity.DELIVER);
        User user1 = new User("1", "321", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "软工三作业检查", 10, 10, 0, 0L, "a", "b", "cc");
        taskMapper.insert(task);
        Report report1 = new Report(user1.getId(), task.getId(), "好垃圾啊", "点进去就闪退", "赢麻了的Mate40 pro plus 12+512");
        reportMapper.insert(report1);
        List<String> list = Arrays.asList("123", "3423", "3213");
        for (String s : list) {
            reportImageMapper.insert(new ReportImage(report1.getId(), s));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("reportId", report1.getId());
        assert reportImageMapper.selectByMap(map).size() == 3;
        for (String s : list) {
            map = new HashMap<>();
            map.put("image", s);
            reportImageMapper.deleteByMap(map);
        }
        assert reportImageMapper.selectByMap(map).size() == 0;
        reportImageMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
        taskMapper.deleteById(task.getId());
        userMapper.deleteById(user0.getId());
        userMapper.deleteById(user1.getId());
    }

    /**
     * 测试为报告插入新的图片
     */
    @Test
    public void testInsert() {
        User user0 = new User("123", "321", UserIdentity.DELIVER);
        User user1 = new User("1", "321", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "软工三作业检查", 10, 10, 0, 0L, "a", "b", "dfs");
        taskMapper.insert(task);
        Report report1 = new Report(user1.getId(), task.getId(), "好垃圾啊", "点进去就闪退", "赢麻了的Mate40 pro plus 12+512");
        reportMapper.insert(report1);
        List<String> list = Arrays.asList("123", "3423", "3213");
        for (String s : list) {
            reportImageMapper.insert(new ReportImage(report1.getId(), s));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("reportId", report1.getId());
        assert reportImageMapper.selectByMap(map).size() == 3;
        reportImageMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
        taskMapper.deleteById(task.getId());
        userMapper.deleteById(user0.getId());
        userMapper.deleteById(user1.getId());
    }

    /**
     * 测试根据map查询报告图片
     */
    @Test
    public void testSelectByMap() {
        User user0 = new User("342", "321", UserIdentity.DELIVER);
        User user1 = new User("34", "321", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "软工三作业检查", 10, 10, 0, 0L, "a", "b", "ddji");
        taskMapper.insert(task);
        Report report1 = new Report(user1.getId(), task.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report1);
        List<String> list = Arrays.asList("123", "3423", "3213");
        for (String s : list) {
            reportImageMapper.insert(new ReportImage(report1.getId(), s));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("reportId", report1.getId());
        assert reportImageMapper.selectByMap(map).size() == 3;
        reportImageMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
        taskMapper.deleteById(task.getId());
        userMapper.deleteById(user0.getId());
        userMapper.deleteById(user1.getId());
    }
}