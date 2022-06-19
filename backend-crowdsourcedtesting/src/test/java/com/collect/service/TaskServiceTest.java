package com.collect.service;

import com.collect.dto.TaskDTO;
import com.collect.dto.UserDTO;
import com.collect.mapper.*;
import com.collect.po.Attribute;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.Work;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.UserIdentity;
import com.collect.util.FileUtil;
import com.collect.vo.ResponseVO;
import com.collect.vo.RuleVO;
import com.collect.vo.TaskVO;
import com.collect.vo.VO;
import com.forte.util.Mock;
import com.forte.util.mockbean.MockObject;
import com.power.common.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    WorkMapper workMapper;

    @Transactional
    @Test
    void testIssueTask() {
        UserDTO userDTO = new UserDTO("tzh19850355091@163.com", "123456", UserIdentity.DELIVER, null);
        userService.register(userDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");

        TaskDTO taskDTO = new TaskDTO(null, userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);

        // System.out.println(taskService.issueTask(taskDTO).getMsg());
        TaskVO taskVO = (TaskVO) (taskService.issueTask(taskDTO).getData().get(0));
        // 获得的任务id
        int taskId = taskVO.getId();
        assert Objects.equals(taskMapper.selectById(taskId).toDTO().getName(), taskDTO.getName());
        // 删除所创建数据
        FileUtil.heavyDelete(FileUtil.TASK_DESC);
//        FileUtil.delete(FileUtil.TASK_DESC,
//                taskMapper.selectById(taskId).toDTO().getPurl());


        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);

        taskMapper.deleteById(taskId);
    }

    @Transactional
    @Test
    void testupdateTaskInfo() {
        // 发布者创建任务
        UserDTO userDTO = new UserDTO("tzh19850355091@163.com", "123456", UserIdentity.DELIVER, null);
        userService.register(userDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");


        TaskDTO taskDTO = new TaskDTO(null, userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);

        TaskVO taskVO = (TaskVO) (taskService.issueTask(taskDTO).getData().get(0));
        // 获得的任务id
        int taskId = taskVO.getId();

        MultipartFile multipartFileNew = FileUtil.getMulFileByPath("src/test/java/com/collect/img/-4248c8a4e6883998.jpg");

        // 更新后的任务内容
        TaskDTO newTaskDTO = new TaskDTO(taskId, userId, "自动化", 100, 50, 1,
                time + time, null, null, null, multipartFileNew, false, false, null, 0, DeviceType.HarmonyOS);


        taskService.updateTaskInfo(newTaskDTO);

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

    @Transactional
    @Test
    void testFindTaskByUserId() {
        // id, userId, name, number, remain, tag, date, aurl, purl, null, null
        // 注册为发包方
        UserDTO userDTO = new UserDTO("tzh19850355091@163.com", "123456", UserIdentity.DELIVER, null);
        userService.register(userDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        // 发布任务并获得任务id存入taskIdList
        long time = System.currentTimeMillis();
        List<Integer> taskIdList = new ArrayList<>();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");

        TaskDTO taskDTO1 = new TaskDTO(null, userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);
        TaskDTO taskDTO2 = new TaskDTO(null, userId, "开发", 90, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);
        TaskDTO taskDTO3 = new TaskDTO(null, userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);

        TaskVO taskVO1 = (TaskVO) (taskService.issueTask(taskDTO1).getData().get(0));
        taskIdList.add(taskVO1.getId());
        TaskVO taskVO2 = (TaskVO) (taskService.issueTask(taskDTO2).getData().get(0));
        taskIdList.add(taskVO2.getId());
        TaskVO taskVO3 = (TaskVO) (taskService.issueTask(taskDTO3).getData().get(0));
        taskIdList.add(taskVO3.getId());
        ResponseVO tasks = taskService.findTaskByUserId(userId);
        List<VO> taskVO = tasks.getData();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (VO vo : taskVO) {
            taskDTOs.add((TaskDTO) vo.toDTO());
        }

        // 判断正确性
        assert Objects.equals(taskVO1.getId(), taskDTOs.get(0).getId());
        assert Objects.equals(taskVO2.getId(), taskDTOs.get(1).getId());
        assert Objects.equals(taskVO3.getId(), taskDTOs.get(2).getId());


        // 删除所创建数据
        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);

        for (int i = 0; i < taskIdList.size(); i++) {
//            System.out.println(FileUtil.getFullPath(FileUtil.TASK_DESC, taskIdList.get(i)));
//            System.out.println(taskDTOs.get(i).getPurl());
            FileUtil.delete(FileUtil.TASK_DESC,
                    taskDTOs.get(i).getPurl());
            taskMapper.deleteById(taskIdList.get(i));
        }


    }

    @Transactional
    @Test
    void testFindTaskByTaskIdList() {
        // id, userId, name, number, remain, tag, date, aurl, purl, null, null
        UserDTO userDTO = new UserDTO("tzh19850355091@163.com", "123456", UserIdentity.DELIVER, null);
        userService.register(userDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        List<Integer> taskIdList = new ArrayList<>();
        long time = System.currentTimeMillis();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");

        TaskDTO taskDTO1 = new TaskDTO(null, userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);
        TaskDTO taskDTO2 = new TaskDTO(null, userId, "开发", 90, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);
        TaskDTO taskDTO3 = new TaskDTO(null, userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);

        TaskVO taskVO1 = (TaskVO) (taskService.issueTask(taskDTO1).getData().get(0));
        taskIdList.add(taskVO1.getId());
        TaskVO taskVO2 = (TaskVO) (taskService.issueTask(taskDTO2).getData().get(0));
        taskIdList.add(taskVO2.getId());
        TaskVO taskVO3 = (TaskVO) (taskService.issueTask(taskDTO3).getData().get(0));
        taskIdList.add(taskVO3.getId());


        List<TaskVO> taskVO = taskService.findTaskByTaskIdList(taskIdList);
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (VO vo : taskVO) {
            taskDTOs.add((TaskDTO) vo.toDTO());
        }

        // 左侧为发布任务时获得的taskid，右侧为通过findTaskByTaskIdList获得的taskid
        assert Objects.equals(taskVO1.getId(), taskDTOs.get(0).getId());
        assert Objects.equals(taskVO2.getId(), taskDTOs.get(1).getId());
        assert Objects.equals(taskVO3.getId(), taskDTOs.get(2).getId());


        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);

        for (int i = 0; i < taskIdList.size(); i++) {
//            System.out.println(FileUtil.getFullPath(FileUtil.TASK_DESC, taskIdList.get(i)));
//            System.out.println(taskDTOs.get(i).getPurl());
            FileUtil.delete(FileUtil.TASK_DESC,
                    taskDTOs.get(i).getPurl());
            taskMapper.deleteById(taskIdList.get(i));
        }
    }

    @Transactional
    @Test
    void testFindAllTasks() {
        // 发布和创建任务
        UserDTO userDTO = new UserDTO("tzh19850355091@163.com", "123456", UserIdentity.DELIVER, null);
        userService.register(userDTO);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "tzh19850355091@163.com");

        List<Integer> taskIdList = new ArrayList<>();
        long time = System.currentTimeMillis();
        int userId = userMapper.selectByMap(map).get(0).getId();
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/软工III-Gitlab项目规范文档.pdf");

        TaskDTO taskDTO1 = new TaskDTO(null, userId, "自动化测试", 100, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);
        TaskDTO taskDTO2 = new TaskDTO(null, userId, "开发", 90, 50, 1,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);
        TaskDTO taskDTO3 = new TaskDTO(null, userId, "占领乌克兰", 90, 50, 2,
                time + time, null, null, null, multipartFile, null, null, null, 0, DeviceType.HarmonyOS);

        TaskVO taskVO1 = (TaskVO) (taskService.issueTask(taskDTO1).getData().get(0));
        taskIdList.add(taskVO1.getId());
        TaskVO taskVO2 = (TaskVO) (taskService.issueTask(taskDTO2).getData().get(0));
        taskIdList.add(taskVO2.getId());
        TaskVO taskVO3 = (TaskVO) (taskService.issueTask(taskDTO3).getData().get(0));
        taskIdList.add(taskVO3.getId());

        ResponseVO tasks = taskService.findAllTasks();
        List<VO> taskVO = tasks.getData();
        List<Integer> taskDTOs = new ArrayList<>();
        for (VO vo : taskVO) {
            TaskDTO tmp = (TaskDTO) vo.toDTO();
            taskDTOs.add(tmp.getId());
        }
        taskDTOs.sort((lhs, rhs) -> {
            if (lhs > rhs) {
                return 1;
            } else {
                return -1;
            }
        });

        assert taskDTOs.size() == taskIdList.size();
        for (int i = 0; i < taskIdList.size(); i++) {
            assert Objects.equals(taskIdList.get(i), taskDTOs.get(i));
        }

        Map<String, Object> map2 = new HashMap<>();
        map2.put("email", "tzh19850355091@163.com");
        userMapper.deleteByMap(map2);

        for (int i = 0; i < taskIdList.size(); i++) {
            TaskDTO taskDTO = (TaskDTO) taskVO.get(i).toDTO();
            FileUtil.heavyDelete(FileUtil.TASK_DESC);
            taskMapper.deleteById(taskIdList.get(i));
        }
    }

    @Transactional
    @Test
    void testSelectTaskByLabel() {
        User user = new User("23", "32312", UserIdentity.DELIVER);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "ss");
        Task task1 = new Task(user.getId(), "MT", 123, 123, 1, time / 4, "dsajdo", "fdejiodfj", "we");
        taskMapper.insert(task);
        taskMapper.insert(task1);
        Integer tag = null;
        Integer if_finished = null;
        assert taskService.selectTaskByLabel(tag, if_finished, null).getData().size() == 2;
        if_finished = 0;
        assert taskService.selectTaskByLabel(tag, if_finished, null).getData().size() == 1;
        if_finished = 1;
        assert taskService.selectTaskByLabel(tag, if_finished, null).getData().size() == 1;
        tag = 0;
        assert taskService.selectTaskByLabel(tag, if_finished, null).getData().size() == 0;
        tag = 1;
        assert taskService.selectTaskByLabel(tag, if_finished, null).getData().size() == 1;
        if_finished = null;
        tag = null;
        String name = "MT";
        assert taskService.selectTaskByLabel(tag, if_finished, name).getData().size() == 2;
        name = "MTC";
        assert taskService.selectTaskByLabel(tag, if_finished, name).getData().size() == 1;
        taskMapper.deleteByMap(null);
        userMapper.deleteByMap(null);
    }

    @Autowired
    TaskMapperWrapper taskMapperWrapper;

    @Autowired
    AttributeMapper attributeMapper;

    @Test
    @Transactional
    void testRecommendAll() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> userTemplate = new HashMap<>();
        userTemplate.put("email", "@email(163,com)");
        userTemplate.put("passwd", "@word(6,16)");
        userTemplate.put("userIdentity", UserIdentity.WORKER);
        Mock.reset(User.class, userTemplate);
        MockObject<User> mockUser = Mock.get(User.class);
        User deliver = mockUser.getOne();
        deliver.setUserIdentity(UserIdentity.DELIVER);
        userMapper.insert(deliver);
        User target = mockUser.getOne();
        userMapper.insert(target);
        List<User> otherWorker = new ArrayList<>();
        // 将目标用户加入
        otherWorker.add(target);
        for (int i = 0; i < 100; i++) {
            User worker = mockUser.getOne();
            userMapper.insert(worker);
            otherWorker.add(worker);
        }
        System.err.println("用户表插入:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        startTime = System.currentTimeMillis();
        int startId = 0;
        Map<String, Object> taskTemplate = new HashMap<>();
        taskTemplate.put("userId", deliver.getId());
        taskTemplate.put("name", "@ctitle(2,6)");
        taskTemplate.put("number", "@integer(50,100)");
        taskTemplate.put("remain", "@integer(0,50)");
        taskTemplate.put("date", System.currentTimeMillis() + 3600 * 1000);
        taskTemplate.put("introduction", "@csentence()");
        Mock.reset(Task.class, taskTemplate);
        for (int i = 0; i < 1000; i++) {
            MockObject<Task> mockTask = Mock.get(Task.class);
            Task task = mockTask.getOne();
            taskMapper.insert(task);
            if (i == 0) {
                startId = task.getId();
            }
        }
        System.err.println("task & attribute 表插入:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        startTime = System.currentTimeMillis();
        Random random = new Random();
        for (User user : otherWorker) {
            int part = Math.abs(random.nextInt()) % 25 + 25;
            for (int i = 0; i < part; i++) {
                Work work = new Work();
                work.setUserId(user.getId());
                work.setTaskId(startId + Math.abs(random.nextInt()) % 1000);
                workMapper.insert(work);
            }
            // 设计角色属性
            Attribute attribute = new Attribute();
            attribute.setUserId(user.getId());
            int sum = part;
            attribute.setHarmonyos(sum -= RandomUtil.randomInt(0, sum));
            attribute.setAndroid(sum -= RandomUtil.randomInt(0, sum));
            attribute.setIos(sum -= RandomUtil.randomInt(0, sum));
            attribute.setLinux(sum -= RandomUtil.randomInt(0, sum));
            attribute.setMac(sum -= RandomUtil.randomInt(0, sum));
            attribute.setWindows(sum);
            sum = part;
            attribute.setFunctional(sum -= RandomUtil.randomInt(0, sum));
            attribute.setBug(sum -= RandomUtil.randomInt(0, sum));
            attribute.setPerformance(sum);
            attribute.setFunctionalAbility(RandomUtil.randomDouble(0, 5) * attribute.getFunctional());
            attribute.setBugAbility(RandomUtil.randomDouble(0, 5) * attribute.getBug());
            attribute.setPerformanceAbility(RandomUtil.randomDouble(0, 5) * attribute.getPerformance());
            attributeMapper.insert(attribute);
        }
        System.err.println("work表插入:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        startTime = System.currentTimeMillis();
//         设置策略
//        User target = new User();
//        target.setId(2);
        List<RuleVO> test1 = new ArrayList<>();
        List<RuleVO> test2 = new ArrayList<>();
        List<RuleVO> test3 = new ArrayList<>();
        RuleVO base = new RuleVO();
        base.setId(1);
        test1.add(base);
        RuleVO cfr = new RuleVO();
        cfr.setId(2);
        test1.add(cfr);
        // 采用策略1和策略2联合执行
        taskMapperWrapper.changeProcessor(test1);
        taskMapperWrapper.recommendAll(target.getId());
        assert taskMapperWrapper.findCurrentRule().size() == 2;
        System.err.println("推荐算法base&cfr:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        startTime = System.currentTimeMillis();
        // 采用策略1执行, 调整参数执行
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("attributeMask", 0b0000000001110);
        base.setExtra(parameter);
        test2.add(base);
        taskMapperWrapper.changeProcessor(test2);
        taskMapperWrapper.recommendAll(target.getId());
        assert !((RuleVO) taskMapperWrapper.findCurrentRule().get(1)).getValid();
        assert (int) ((RuleVO) taskMapperWrapper.findCurrentRule().get(0)).getExtra().get("attributeMask") == 0b0000000001110;
        System.err.println("推荐算法base(调整参数):" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        startTime = System.currentTimeMillis();
        // 采用策略2执行
        test3.add(cfr);
        taskMapperWrapper.changeProcessor(test3);
        taskMapperWrapper.recommendAll(target.getId());
        assert ((RuleVO) taskMapperWrapper.findCurrentRule().get(0)).getId().equals(2);
        System.err.println("推荐算法cfr:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }
}

