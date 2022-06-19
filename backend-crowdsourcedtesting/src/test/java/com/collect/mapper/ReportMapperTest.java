package com.collect.mapper;

import com.collect.po.Report;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.Work;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
public class ReportMapperTest {

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    WorkMapper workMapper;

    /**
     * 测试根据taskId查询task
     */
    @Test
    public void testSelectByTaskId() {
        User user0 = new User("342", "321", UserIdentity.DELIVER);
        User user1 = new User("34", "321", UserIdentity.WORKER);
        User user2 = new User("34333", "321", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        userMapper.insert(user2);
        Task task = new Task(user0.getId(), "软工三作业检查", 10, 10, 0, 0L, "a", "b", "dfs");
        taskMapper.insert(task);
        Report report1 = new Report(user1.getId(), task.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report1);
        Report report2 = new Report(user2.getId(), task.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report2);
        Map<String, Object> map = new HashMap<>();
        map.put("taskId", task.getId());
        assert reportMapper.selectByMap(map).size() == 2;
        reportMapper.deleteByMap(null);
        taskMapper.deleteById(task.getId());
        userMapper.deleteById(user0.getId());
        userMapper.deleteById(user1.getId());
        userMapper.deleteById(user2.getId());
    }

    /**
     * 测试selectLastByUserIdAndTaskId，根据用户id和taskId查询用户最近提交的报告
     */
    @Test
    @Transactional
    void testSelectLastByUserIdAndTaskId() {
        final String password = "password", name = "name";
        User worker = new User("1", password, UserIdentity.WORKER);
        User deliver = new User("2", password, UserIdentity.DELIVER);
        userMapper.insert(worker);
        userMapper.insert(deliver);
        // 插入任务
        Task task1 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "feji");
        taskMapper.insert(task1);
        // 插入工作
        workMapper.insert(new Work(null, worker.getId(), task1.getId(), WorkStatus.TO_FINISH));
        // 插入报告1
        Report report1 = new Report(worker.getId(), task1.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report1);
        // 最近的报告是报告1
        assert Objects.equals(reportMapper.selectLastByUserIdAndTaskId(worker.getId(), task1.getId()).getId(), report1.getId());
        // 插入报告2
        Report report2 = new Report(worker.getId(), task1.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report2);
        // 最近的报告是报告2
        assert Objects.equals(reportMapper.selectLastByUserIdAndTaskId(worker.getId(), task1.getId()).getId(), report2.getId());
    }
}
