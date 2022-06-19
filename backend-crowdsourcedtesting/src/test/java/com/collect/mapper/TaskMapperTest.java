package com.collect.mapper;

import com.collect.po.Attribute;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.UserIdentity;
import com.forte.util.Mock;
import com.forte.util.mockbean.MockObject;
import com.sun.tracing.dtrace.Attributes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TaskMapperTest {

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AttributeMapper attributeMapper;

    /**
     * 测试task插入
     */
    @Test
    @Transactional
    void testInsert() {
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        Task task = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "ejwi");
        assert 1 == taskMapper.insert(task);
    }

    /**
     * 测试根据ID更新task
     */
    @Test
    @Transactional
    void testUpdateById() {
        System.out.println("wdnmd");
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        Task task = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "dhi");
        taskMapper.insert(task);
        int t = task.getId();
        task.setId(234325423);
        assert 0 == taskMapper.updateById(task);
        task.setId(t);
        task.setAurl("67");
        System.out.println(task);
        assert 1 == taskMapper.updateById(task);
        System.out.println(taskMapper.selectById(task.getId()));
        assert "67".equals(taskMapper.selectById(task.getId()).getAurl());
    }

    /**
     * 根据map查询条件查询task
     */
    @Test
    @Transactional
    void testSelectByMap() {
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        Task task = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "wdioj");
        taskMapper.insert(task);
        Map<String, Object> select = new HashMap<>();
        select.put("id", task.getId());
        select.put("userId", task.getUserId());
        assert taskMapper.selectByMap(select).size() == 1;
        select.remove("id");
        Task task2 = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "fejo");
        taskMapper.insert(task2);
        assert taskMapper.selectByMap(select).size() == 2;
        Task task3 = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "efjoi");
        taskMapper.insert(task3);
        assert taskMapper.selectByMap(null).size() == 3;
    }

    /**
     * 测试selectTaskByLabel方法，根据标签查询任务
     */
    @Test
    @Transactional
    void testSelectTaskByLabel() {
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        Task task1 = new Task(user.getId(), "MT", 123, 123, 1, time / 4, "dsajdo", "fdejiodfj", "feji");
        taskMapper.insert(task);
        taskMapper.insert(task1);
        Integer tag = null;
        Integer if_finished = null;
        long now_time = System.currentTimeMillis();
        assert taskMapper.selectTaskByLabel(tag, if_finished, null, now_time).size() == 2;
        if_finished = 0;
        assert taskMapper.selectTaskByLabel(tag, if_finished, null, now_time).size() == 1;
        if_finished = 1;
        assert taskMapper.selectTaskByLabel(tag, if_finished, null, now_time).size() == 1;
        tag = 0;
        assert taskMapper.selectTaskByLabel(tag, if_finished, null, now_time).size() == 0;
        tag = 1;
        assert taskMapper.selectTaskByLabel(tag, if_finished, null, now_time).size() == 1;
        if_finished = null;
        tag = null;
        String name = "MT";
        assert taskMapper.selectTaskByLabel(tag, if_finished, name, now_time).size() == 2;
        name = "MTC";
        assert taskMapper.selectTaskByLabel(tag, if_finished, name, now_time).size() == 1;
    }

    /**
     * 测试推荐全部任务，数据量小的情况下，和selectAll结果相似
     */
    @Test
    @Transactional
    void testRecommendAll() {
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        Attribute attribute = new Attribute();
        attribute.setUserId(user.getId());
        attributeMapper.insert(attribute);

        Task task = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "wdioj");
        taskMapper.insert(task);
        assert taskMapper.recommendAll(user.getId()).size() == 1;
        Task task2 = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "fejo");
        taskMapper.insert(task2);
        assert taskMapper.recommendAll(user.getId()).size() == 2;
        Task task3 = new Task(user.getId(), "213d", 123, 123, 0, 0L, "dsajdo", "fdejiodfj", "efjoi");
        taskMapper.insert(task3);
        assert taskMapper.recommendAll(user.getId()).size() == 3;
    }

    /**
     * 测试根据标签推荐任务，数据量大的情况下，只返回部分推荐数据
     */
    @Test
    @Transactional
    void testRecommendTaskByLabel() {
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        Attribute attribute = new Attribute();
        attribute.setUserId(user.getId());
        attributeMapper.insert(attribute);

        long time = System.currentTimeMillis() * 2;
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        Task task1 = new Task(user.getId(), "MT", 123, 123, 1, time / 4, "dsajdo", "fdejiodfj", "feji");
        taskMapper.insert(task);
        taskMapper.insert(task1);

        Integer tag = null;
        Integer if_finished = null;
        long now_time = System.currentTimeMillis();
        assert taskMapper.recommendTaskByLabel(tag, if_finished, now_time, user.getId()).size() == 2;
        if_finished = 0;
        assert taskMapper.recommendTaskByLabel(tag, if_finished, now_time, user.getId()).size() == 1;
        if_finished = 1;
        assert taskMapper.recommendTaskByLabel(tag, if_finished, now_time, user.getId()).size() == 1;
        tag = 0;
        assert taskMapper.recommendTaskByLabel(tag, if_finished, now_time, user.getId()).size() == 0;
        tag = 1;
        assert taskMapper.recommendTaskByLabel(tag, if_finished, now_time, user.getId()).size() == 1;


        User deliver = new User();
        deliver.setPasswd("passwd");
        deliver.setName("deliver");
        deliver.setEmail("deliver_email");
        deliver.setUserIdentity(UserIdentity.DELIVER);

        userMapper.insert(deliver);

        Map<String, Object> taskTemplate = new HashMap<>();
        taskTemplate.put("userId", deliver.getId());
        taskTemplate.put("name", "@ctitle(2,6)");
        taskTemplate.put("number", "@integer(50,100)");
        taskTemplate.put("remain", "@integer(0,50)");
        taskTemplate.put("date", System.currentTimeMillis() + 3600 * 1000);
        taskTemplate.put("introduction", "@csentence()");
        taskTemplate.put("tag", tag);
        Mock.reset(Task.class, taskTemplate);
        MockObject<Task> mockTask = Mock.get(Task.class);

        for (int i = 0; i < 500; i++) {
            taskMapper.insert(mockTask.getOne());
        }

        List<Task> tasks = taskMapper.recommendTaskByLabel(tag, 0, System.currentTimeMillis(), user.getId());
        assert tasks.size() == 200;
    }
}
