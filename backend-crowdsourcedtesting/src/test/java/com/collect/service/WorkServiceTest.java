package com.collect.service;

import com.collect.dto.ReportDTO;
import com.collect.dto.TaskDTO;
import com.collect.dto.WorkDTO;
import com.collect.mapper.*;
import com.collect.po.Attribute;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.Work;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.vo.TaskVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class WorkServiceTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    WorkService workService;

    @Autowired
    WorkMapper workMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    ReportService reportService;

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    AttributeMapper attributeMapper;

    /**
     * 测试根据userId查询发布的任务
     */
    @Transactional
    @Test
    public void testFindTaskByUserId() {
        User user0 = new User("666", "jdjwdfnm", UserIdentity.DELIVER);
        User user1 = new User("waoij", "fejiojf", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "jeiw", 10, 10, 1, System.currentTimeMillis() + 100000, "dea", "ejijfw", "wo");
        taskMapper.insert(task);
        workMapper.insert(new Work(null, user1.getId(), task.getId(), WorkStatus.TO_FINISH));
        assert workService.findTaskByUserId(user1.getId(), WorkStatus.FINISH).getData().size() == 0;
        assert workService.findTaskByUserId(user1.getId(), WorkStatus.TO_FINISH).getData().size() == 1;
    }

    /**
     * 测试参加任务
     */
    @Transactional
    @Test
    public void testPartTask() {
        User user0 = new User("666", "jdjwdfnm", UserIdentity.DELIVER);
        User user1 = new User("waoij", "fejiojf", UserIdentity.WORKER);
        User user2 = new User("ooj", "foiwjf", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user2);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "jeiw", 10, 10, 1, 0L, "dea", "ejijfw", "fej");
        Task task2 = new Task(user0.getId(), "jeiw", 10, 10, 1, System.currentTimeMillis() + 2743892784327L, "dea", "ejijfw", "wo");
        taskMapper.insert(task);
        taskMapper.insert(task2);
        assert workService.partTask(new WorkDTO(user1.getId(), task.getId(), WorkStatus.FINISH)).getCode() == 6002;
        assert workService.partTask(new WorkDTO(user1.getId(), task2.getId(), WorkStatus.TO_FINISH)).getCode() == 4000;
        assert workService.partTask(new WorkDTO(user1.getId(), task2.getId(), WorkStatus.TO_FINISH)).getCode() == 6001;
    }

    /**
     * 测试定时任务，不能加@Transaction注解，已手动删除数据
     */
    @Test
    public void testTaskFinish() {
        // 创建用户，发包方，工人
        User deliver = new User();
        deliver.setPasswd("passwd");
        deliver.setName("deliver");
        deliver.setEmail("deliver_email");
        deliver.setUserIdentity(UserIdentity.DELIVER);

        User worker = new User();
        worker.setPasswd("passwd");
        worker.setName("worker");
        worker.setEmail("worker_email");
        worker.setUserIdentity(UserIdentity.DELIVER);

        userMapper.insert(deliver);
        userMapper.insert(worker);

        // 为工人创建属性条目
        Attribute attribute = new Attribute();
        attribute.setUserId(worker.getId());
        attributeMapper.insert(attribute);

        // 创建任务1，任务2，任务1在3s后过期，任务2在8s后过期
        TaskDTO taskOverTime = new TaskDTO();
        taskOverTime.setUserId(deliver.getId());
        taskOverTime.setName("taskOverTime");
        taskOverTime.setNumber(100);
        taskOverTime.setTag(1);
        taskOverTime.setDevice(DeviceType.HarmonyOS);
        taskOverTime.setIntroduction("introduction");
        taskOverTime.setDate(System.currentTimeMillis() + 1000 * 3);

        TaskDTO taskInTime = new TaskDTO();
        taskInTime.setUserId(deliver.getId());
        taskInTime.setName("taskInTime");
        taskInTime.setNumber(100);
        taskInTime.setTag(1);
        taskInTime.setDevice(DeviceType.HarmonyOS);
        taskInTime.setIntroduction("introduction");
        taskInTime.setDate(System.currentTimeMillis() + 1000 * 8);

        int taskOverTimeId = ((TaskVO) taskService.issueTask(taskOverTime).getData().get(0)).getId();
        int taskInTimeId = ((TaskVO) taskService.issueTask(taskInTime).getData().get(0)).getId();

        // 工人领取任务1，任务2
        WorkDTO partTaskOverTime = new WorkDTO();
        partTaskOverTime.setTaskId(taskOverTimeId);
        partTaskOverTime.setUserId(worker.getId());
        workService.partTask(partTaskOverTime);

        WorkDTO partTaskInTime = new WorkDTO();
        partTaskInTime.setTaskId(taskInTimeId);
        partTaskInTime.setUserId(worker.getId());
        workService.partTask(partTaskInTime);

        // 5s后，任务1过期
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 工作1失败，工作2仍待完成
        assert workService.findWorkStatus(worker.getId(), taskOverTimeId).equals(WorkStatus.FAIL);
        assert workService.findWorkStatus(worker.getId(), taskInTimeId).equals(WorkStatus.TO_FINISH);

        // 工人为工作2提交报告
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setUserId(worker.getId());
        reportDTO.setTaskId(taskInTimeId);
        reportDTO.setSteps("steps");
        reportDTO.setNote("note");
        reportService.commitReport(reportDTO);

        // 5s后，任务2结束，工作2完成
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 两个工作结束，工人属性增加2
        Map<String, Object> selectByUserId = new HashMap<>();
        selectByUserId.put("userId", worker.getId());
        assert workService.findWorkStatus(worker.getId(), taskInTimeId).equals(WorkStatus.FINISH);
        assert attributeMapper.selectByMap(selectByUserId).get(0).getHarmonyos() == 2;

        // 删除用户
        userMapper.deleteById(worker.getId());
        userMapper.deleteById(deliver.getId());

        // 删除任务
        taskMapper.deleteById(taskInTimeId);
        taskMapper.deleteById(taskOverTimeId);

        // 删除工作
        Map<String, Object> deleteByUserId = new HashMap<>();
        deleteByUserId.put("userId", worker.getId());
        workMapper.deleteByMap(deleteByUserId);

        // 删除报告
        reportMapper.deleteByMap(deleteByUserId);

        //删除属性
        attributeMapper.deleteByMap(deleteByUserId);

    }

}
