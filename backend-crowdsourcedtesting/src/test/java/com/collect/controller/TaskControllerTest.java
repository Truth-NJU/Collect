package com.collect.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.collect.dto.TaskDTO;
import com.collect.dto.WorkDTO;
import com.collect.mapper.AttributeMapper;
import com.collect.mapper.TaskMapperWrapper;
import com.collect.mapper.UserMapper;
import com.collect.mapper.WorkMapper;
import com.collect.po.Attribute;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.Work;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.service.TaskService;
import com.collect.service.UserService;
import com.collect.service.WorkService;
import com.collect.util.FileUtil;
import com.collect.vo.ResponseVO;
import com.collect.vo.TaskVO;
import com.collect.vo.UserVO;
import com.collect.vo.VO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@SpringBootTest
@Transactional
public class TaskControllerTest {


    @Autowired
    TaskService taskService;
    @Autowired
    UserService userService;

    @Autowired
    WorkService workService;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    WorkMapper workMapper;

    @Autowired
    AttributeMapper attributeMapper;

    @Test
    void testSearchPublishedTask() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        UserController userController = new UserController();
        userController.userService = userService;

        // 发布方查看自己的发布的任务

        // 发布任务
        UserVO userVo = new UserVO("tzh19850355091@163.com", "123456", null, UserIdentity.DELIVER);
        userController.register(userVo);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        List<Integer> taskIdList = new ArrayList<>();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");
        TaskVO taskVO1 = new TaskVO(null,userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, null, null,null, null, null, null,0, DeviceType.HarmonyOS);
        TaskVO taskVO2 = new TaskVO(null,userId, "开发", 90, 50, 1,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO3 = new TaskVO(null,userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);

        TaskVO task1 = (TaskVO) (taskController.issueTask(taskVO1).getData().get(0));
        taskIdList.add(task1.getId());
        TaskVO task2 = (TaskVO) (taskController.issueTask(taskVO2).getData().get(0));
        taskIdList.add(task2.getId());
        TaskVO task3 = (TaskVO) (taskController.issueTask(taskVO3).getData().get(0));
        taskIdList.add(task3.getId());
        ResponseVO tasks = taskController.searchPublishedTask(userId);
        List<VO> taskVO = tasks.getData();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (VO vo : taskVO) {
            taskDTOs.add((TaskDTO) vo.toDTO());
        }


        //System.out.println(taskVO1.getId()+" "+taskDTOs.get(0).getId());
        // 判断正确性
        assert Objects.equals(task1.getId(), taskDTOs.get(0).getId());
        assert Objects.equals(task2.getId(), taskDTOs.get(1).getId());
        assert Objects.equals(task3.getId(), taskDTOs.get(2).getId());


        for (int i = 0; i < taskIdList.size(); i++) {
//            System.out.println(FileUtil.getFullPath(FileUtil.TASK_DESC, taskIdList.get(i)));
//            System.out.println(taskDTOs.get(i).getPurl());
            FileUtil.delete(FileUtil.TASK_DESC,
                    taskDTOs.get(i).getPurl());
            taskMapper.deleteById(taskIdList.get(i));
        }

        // 删除所创建数据
        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);
    }

    // 众包工人查看正在执行的任务
    @Test
    void testSearchRunningTask() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        UserController userController = new UserController();
        userController.userService = userService;

        // 发包方发布任务
        UserVO userVo = new UserVO("tzh19850355091@163.com", "123456", null, UserIdentity.DELIVER);
        userController.register(userVo);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        List<Integer> taskIdList = new ArrayList<>();
        int userId = userMapper.selectByMap(map).get(0).getId();
        // MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");
        TaskVO taskVO1 = new TaskVO(null,userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, null, null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO2 = new TaskVO(null,userId, "开发", 90, 50, 1,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO3 = new TaskVO(null,userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);

        TaskVO task1 = (TaskVO) (taskController.issueTask(taskVO1).getData().get(0));
        taskIdList.add(task1.getId());
        TaskVO task2 = (TaskVO) (taskController.issueTask(taskVO2).getData().get(0));
        taskIdList.add(task2.getId());
        TaskVO task3 = (TaskVO) (taskController.issueTask(taskVO3).getData().get(0));
        taskIdList.add(task3.getId());

        // 注册众包工人
        UserVO worker = new UserVO("123@qq.com", "123456", null, UserIdentity.WORKER);
        userController.register(worker);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("email", "123@qq.com");
        int workerId = userMapper.selectByMap(map3).get(0).getId();

        // 众包工人领取任务0
        WorkDTO workDTO1 = new WorkDTO(workerId, taskIdList.get(0));
        workService.partTask(workDTO1);

        // 众包工人领取任务1
        WorkDTO workDTO2 = new WorkDTO(workerId, taskIdList.get(1));
        workService.partTask(workDTO2);


        // 众包工人完成任务0
        Work work = workDTO1.toPO();
        UpdateWrapper<Work> updateWrapper = new UpdateWrapper<>();
        Map<String, Object> map4 = new HashMap<>();
        map4.put("userId", work.getUserId());
        map4.put("taskId", work.getTaskId());
        updateWrapper.allEq(map4);
        Work w1 = new Work();
        w1.setFinish(WorkStatus.FINISH);
        workMapper.update(w1, updateWrapper);

        // 得到正在执行的任务的id（这里数量为1）
        List<VO> taskLists = taskController.searchTaskForWorker(workerId, WorkStatus.FINISH).getData();
        TaskVO taskVO = (TaskVO) taskLists.get(0);
//        assert Objects.equals(taskIdList.get(1), taskVO.getId());

        // 删除所创建数据

        // 删除创建的用户
        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);
        userMapper.deleteById(workerId);

        // 删除任务
        for (Integer taskId : taskIdList) {
            taskMapper.deleteById(taskId);
        }

        // 删除work表的数据
        Map<String, Object> map5 = new HashMap<>();
        map5.put("userId", workerId);
        workMapper.deleteByMap(map4);

    }

    // 测试众包工人已经完成的任务
    @Test
    void testSearchFinishedTask() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        UserController userController = new UserController();
        userController.userService = userService;

        // 发包方发布任务
        UserVO userVo = new UserVO("tzh19850355091@163.com", "123456", null, UserIdentity.DELIVER);
        userController.register(userVo);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        List<Integer> taskIdList = new ArrayList<>();
        int userId = userMapper.selectByMap(map).get(0).getId();
        TaskVO taskVO1 = new TaskVO(null,userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, null, null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO2 = new TaskVO(null,userId, "开发", 90, 50, 1,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO3 = new TaskVO(null,userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);


        TaskVO task1 = (TaskVO) (taskController.issueTask(taskVO1).getData().get(0));
        taskIdList.add(task1.getId());
        TaskVO task2 = (TaskVO) (taskController.issueTask(taskVO2).getData().get(0));
        taskIdList.add(task2.getId());
        TaskVO task3 = (TaskVO) (taskController.issueTask(taskVO3).getData().get(0));
        taskIdList.add(task3.getId());

        // 注册众包工人
        UserVO worker = new UserVO("1923928217@qq.com", "123456", null, UserIdentity.WORKER);
        userController.register(worker);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("email", "1923928217@qq.com");
        int workerId = userMapper.selectByMap(map3).get(0).getId();

        // 众包工人领取任务0
        WorkDTO workDTO1 = new WorkDTO(workerId, taskIdList.get(0));
        workService.partTask(workDTO1);

        // 众包工人领取任务1
        WorkDTO workDTO2 = new WorkDTO(workerId, taskIdList.get(1));
        workService.partTask(workDTO2);

        // 众包工人完成任务0
        Work work = workDTO1.toPO();
        UpdateWrapper<Work> updateWrapper = new UpdateWrapper<>();
        Map<String, Object> map4 = new HashMap<>();
        map4.put("userId", work.getUserId());
        map4.put("taskId", work.getTaskId());
        updateWrapper.allEq(map4);
        Work w1 = new Work();
        w1.setFinish(WorkStatus.FINISH);
        workMapper.update(w1, updateWrapper);


        // 得到正在执行的任务的id（这里数量为1）
        // System.out.println(taskController.searchFininshedTask(workerId,WorkStatus.TO_FINISH).getMsg());
        List<VO> taskLists = taskController.searchTaskForWorker(workerId, WorkStatus.FINISH).getData();
        TaskVO taskVO = (TaskVO) taskLists.get(0);
        assert Objects.equals(taskIdList.get(0), taskVO.getId());

        //删除所创建数据

        // 删除创建的用户
        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);
        userMapper.deleteById(workerId);


        // 删除work表的数据
        Map<String, Object> map5 = new HashMap<>();
        map5.put("userId", workerId);
        workMapper.deleteByMap(map4);
    }


    @Test
    void testUpdateTaskInfo() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        UserController userController = new UserController();
        userController.userService = userService;

        // 发布任务
        UserVO userVo = new UserVO("tzh19850355091@163.com", "123456", null, UserIdentity.DELIVER);
        userController.register(userVo);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        long time = System.currentTimeMillis();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");
        TaskVO taskVO = new TaskVO(null,userId, "自动化测试", 100, 50, 1,
                time + time,null, null,null, null, multipartFile,null, null,null,null,0, DeviceType.HarmonyOS);

        TaskVO task = (TaskVO) (taskController.issueTask(taskVO).getData().get(0));
        // 获得的任务id
        int taskId = task.getId();

        // System.out.println(taskId);
        // 更新后的任务内容
        MultipartFile multipartFileNew = FileUtil.getMulFileByPath("src/test/java/com/collect/img/-4248c8a4e6883998.jpg");
        TaskVO newTaskVO = new TaskVO(taskId, userId, "自动化", 100, 50, 1,
                time + time, null, null,null, null,multipartFileNew, false, false,null, null,0,DeviceType.HarmonyOS);


        taskController.updateTaskInfo(newTaskVO);

        assert Objects.equals(taskMapper.selectById(taskId).toDTO().getName(), "自动化");


        // 删除所创建数据
        FileUtil.heavyDelete(FileUtil.TASK_DESC);
//        FileUtil.delete(FileUtil.TASK_DESC,
//                taskMapper.selectById(taskId).toDTO().getPurl());


        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);


        taskMapper.deleteById(taskId);

    }


    @Test
    void testIssueTask() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        UserController userController = new UserController();
        userController.userService = userService;


        // 发布任务
        UserVO userVo = new UserVO("tzh19850355091@163.com", "123456", null, UserIdentity.DELIVER);
        userController.register(userVo);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        long time = System.currentTimeMillis();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");
        TaskVO taskVO = new TaskVO(null,userId, "自动化测试", 100, 50, 1,
                time + time,null, null,null, null, multipartFile,null, null,null,null,0, DeviceType.HarmonyOS);

        TaskVO task = (TaskVO) (taskController.issueTask(taskVO).getData().get(0));
        // 获得的任务id
        int taskId = task.getId();
        assert Objects.equals(taskMapper.selectById(taskId).toDTO().getName(), task.getName());


        // 删除所创建数据
        FileUtil.heavyDelete(FileUtil.TASK_DESC);
//        FileUtil.delete(FileUtil.TASK_DESC,
//                taskMapper.selectById(taskId).toDTO().getPurl());


        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);

        taskMapper.deleteById(taskId);
    }


    // 任务广场
    @Test
    void testFindAllTasks() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        UserController userController = new UserController();
        userController.userService = userService;


        // 发布任务
        UserVO userVo = new UserVO("tzh19850355091@163.com", "123456", null, UserIdentity.DELIVER);
        userController.register(userVo);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        List<Integer> taskIdList = new ArrayList<>();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");
        TaskVO taskVO1 = new TaskVO(null,userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, null, null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO2 = new TaskVO(null,userId, "开发", 90, 50, 1,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);
        TaskVO taskVO3 = new TaskVO(null,userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, null,null,null, null, null, null,0,DeviceType.HarmonyOS);


        TaskVO task1 = (TaskVO) (taskController.issueTask(taskVO1).getData().get(0));
        taskIdList.add(task1.getId());
        TaskVO task2 = (TaskVO) (taskController.issueTask(taskVO2).getData().get(0));
        taskIdList.add(task2.getId());
        TaskVO task3 = (TaskVO) (taskController.issueTask(taskVO3).getData().get(0));
        taskIdList.add(task3.getId());
        ResponseVO tasks = taskController.findAllTasks();
        List<VO> taskVO = tasks.getData();
        List<Integer> taskDTOs = new ArrayList<>();

        for (VO vo : taskVO) {
            TaskDTO tmp = (TaskDTO) vo.toDTO();
            taskDTOs.add(tmp.getId());
        }
        taskDTOs.sort(Comparator.comparingInt(a -> a));
        assert taskDTOs.size() == taskIdList.size();
        for (int i = 0; i < taskIdList.size(); i++) {
            assert Objects.equals(taskIdList.get(i), taskDTOs.get(i));
        }

        // 删除所创建数据
        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);

        FileUtil.heavyDelete(FileUtil.TASK_DESC);
//
//        for (Integer integer : taskIdList) {
//            taskMapper.deleteById(integer);
//        }
    }

    @Test
    void testSelectTaskByLabel() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "cc");
        Task task1 = new Task(user.getId(), "MT", 123, 123, 1, time / 4, "dsajdo", "fdejiodfj", "cc");
        taskMapper.insert(task);
        taskMapper.insert(task1);
        Integer tag = null;
        Integer if_finished = null;
        assert taskController.selectTaskByLabel(tag, if_finished, null).getData().size() == 2;
        if_finished = 0;
        assert taskController.selectTaskByLabel(tag, if_finished, null).getData().size() == 1;
        if_finished = 1;
        assert taskController.selectTaskByLabel(tag, if_finished, null).getData().size() == 1;
        tag = 0;
        assert taskController.selectTaskByLabel(tag, if_finished, null).getData().size() == 0;
        tag = 1;
        assert taskController.selectTaskByLabel(tag, if_finished, null).getData().size() == 1;
        if_finished = null;
        tag = null;
        String name = "MT";
        assert taskController.selectTaskByLabel(tag, if_finished, name).getData().size() == 2;
        name = "MTC";
        assert taskController.selectTaskByLabel(tag, if_finished, name).getData().size() == 1;
        taskMapper.deleteByMap(null);
        userMapper.deleteByMap(null);
    }

    @Transactional
    @Test
    void testRecommendTasksByLabel() {
        TaskController taskController = new TaskController();
        taskController.taskService = taskService;
        taskController.userService = userService;
        taskController.workService = workService;
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        Attribute attribute=new Attribute();
        attribute.setUserId(user.getId());
        attributeMapper.insert(attribute);
        long time = System.currentTimeMillis() * 2;
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "cc");
        Task task1 = new Task(user.getId(), "MT", 123, 123, 1, time / 4, "dsajdo", "fdejiodfj", "cc");
        taskMapper.insert(task);
        taskMapper.insert(task1);
        Integer tag = null;
        Integer if_finished = null;
        assert taskController.recommendTaskByLabel(tag, if_finished, user.getId()).getData().size() == 2;
        if_finished = 0;
        assert taskController.recommendTaskByLabel(tag, if_finished, user.getId()).getData().size() == 1;
        if_finished = 1;
        assert taskController.recommendTaskByLabel(tag, if_finished, user.getId()).getData().size() == 1;
        tag = 0;
        assert taskController.recommendTaskByLabel(tag, if_finished, user.getId()).getData().size() == 0;
        tag = 1;
        assert taskController.recommendTaskByLabel(tag, if_finished, user.getId()).getData().size() == 1;
        taskMapper.deleteByMap(null);
        userMapper.deleteByMap(null);
    }
}
