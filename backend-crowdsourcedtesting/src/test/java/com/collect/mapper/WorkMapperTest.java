package com.collect.mapper;

import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.UserWorkList;
import com.collect.po.Work;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest
public class WorkMapperTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    WorkMapper workMapper;

    /**
     * 测试插入新WORK
     */
    @Test
    @Transactional
    public void testInsert() {
        User user0 = new User("666", "jdjwdfnm", UserIdentity.DELIVER);
        User user1 = new User("waoij", "fejiojf", UserIdentity.WORKER);
        User user2 = new User("ooj", "foiwjf", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user2);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "jeiw", 10, 10, 1, 0L, "dea", "ejijfw", "djo");
        taskMapper.insert(task);
        assert 1 == workMapper.insert(new Work(null, user1.getId(), task.getId(), WorkStatus.TO_FINISH));
        workMapper.deleteByMap(null);
        taskMapper.deleteByMap(null);
        userMapper.deleteByMap(null);
    }

    /**
     * 测试根据map查询work
     */
    @Test
    @Transactional
    public void testSelectByMap() {
        User user0 = new User("666", "jdjwdfnm", UserIdentity.DELIVER);
        User user1 = new User("waoij", "fejiojf", UserIdentity.WORKER);
        User user2 = new User("ooj", "foiwjf", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user2);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "jeiw", 10, 10, 1, 0L, "dea", "ejijfw", "feji");
        Task task2 = new Task(user0.getId(), "jeiw", 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        taskMapper.insert(task);
        taskMapper.insert(task2);
        workMapper.insert(new Work(null, user1.getId(), task.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user1.getId(), task2.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user2.getId(), task.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user2.getId(), task2.getId(), WorkStatus.TO_FINISH));
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user1.getId());
        assert 2 == workMapper.selectByMap(map).size();
        map.put("taskId", task.getId());
        assert 1 == workMapper.selectByMap(map).size();
        workMapper.deleteByMap(null);
        taskMapper.deleteByMap(null);
        userMapper.deleteByMap(null);
    }

    /**
     * 测试findUsersTaskPartIn方法，查询参与任务x的全部用户
     */
    @Transactional
    @Test
    public void testFindUsersTaskPartIn() {
        final String password = "password", name = "name";
        User deliver = new User("1", password, UserIdentity.DELIVER);
        User user1 = new User("2", password, UserIdentity.WORKER);
        User user2 = new User("3", password, UserIdentity.WORKER);
        User user3 = new User("4", password, UserIdentity.WORKER);
        userMapper.insert(deliver);
        userMapper.insert(user1);
        userMapper.insert(user2);
        userMapper.insert(user3);
        Task task1 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "feji");
        Task task2 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        Task task3 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        Task task4 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        taskMapper.insert(task1);
        taskMapper.insert(task2);
        taskMapper.insert(task3);
        taskMapper.insert(task4);
        // user1 work in task1, task2
        workMapper.insert(new Work(null, user1.getId(), task1.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user1.getId(), task2.getId(), WorkStatus.TO_FINISH));
        // user2 work in task1, task2, task3
        workMapper.insert(new Work(null, user2.getId(), task1.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user2.getId(), task2.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user2.getId(), task3.getId(), WorkStatus.TO_FINISH));
        // user3 work in task1, task2, task3, task4
        workMapper.insert(new Work(null, user3.getId(), task1.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user3.getId(), task2.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user3.getId(), task3.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, user3.getId(), task4.getId(), WorkStatus.TO_FINISH));
        List<Integer> list = new ArrayList<>();
        list.add(task1.getId());
        int cnt = 0;
        for (UserWorkList workList : workMapper.findUsersTaskPartIn(list)) {
            cnt += workList.getTaskId().size();
        }
        assert cnt == (2 + 3 + 4);
    }

    /**
     * 测试selectPartTasksByUserId，根据用户id查询用户参与的工作
     */
    @Test
    @Transactional
    void testSelectPartTasksByUserId() {
        // 插入角色
        final String password = "password", name = "name";
        User worker = new User("1", password, UserIdentity.WORKER);
        User deliver = new User("2", password, UserIdentity.DELIVER);
        userMapper.insert(worker);
        userMapper.insert(deliver);
        // 插入任务
        Task task1 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "feji");
        Task task2 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        Task task3 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        Task task4 = new Task(deliver.getId(), name, 10, 10, 1, 0L, "dea", "ejijfw", "fejo");
        taskMapper.insert(task1);
        taskMapper.insert(task2);
        taskMapper.insert(task3);
        taskMapper.insert(task4);
        // 插入工作1
        workMapper.insert(new Work(null, worker.getId(), task1.getId(), WorkStatus.TO_FINISH));
        // 只有一个工作被查出
        List<Integer> res = workMapper.selectPartTasksByUserId(worker.getId());
        assert res.size() == 1 && Objects.equals(res.get(0), task1.getId());
        int[] predictTaskIdList = new int[]{task1.getId(), task2.getId(), task3.getId(), task4.getId()};
        // 插入工作2，3，4
        workMapper.insert(new Work(null, worker.getId(), task2.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, worker.getId(), task3.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, worker.getId(), task4.getId(), WorkStatus.TO_FINISH));
        res = workMapper.selectPartTasksByUserId(worker.getId());
        // 4个任务均被查出
        for (int i = 0; i < predictTaskIdList.length; i++) {
            assert predictTaskIdList[i] == res.get(i);
        }
    }

}
