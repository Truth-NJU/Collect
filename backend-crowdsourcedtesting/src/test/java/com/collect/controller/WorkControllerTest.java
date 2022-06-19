package com.collect.controller;

import com.collect.mapper.TaskMapperWrapper;
import com.collect.mapper.UserMapper;
import com.collect.mapper.WorkMapper;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.service.WorkService;
import com.collect.vo.WorkVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class WorkControllerTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    WorkService workService;

    @Autowired
    WorkMapper workMapper;

    @Transactional
    @Test
    public void testPartTask(){
        WorkController workController=new WorkController();
        workController.workService=workService;
        User user0=new User("666","jdjwdfnm", UserIdentity.DELIVER);
        User user1=new User("waoij","fejiojf",UserIdentity.WORKER);
        User user2=new User("ooj","foiwjf",UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user2);
        userMapper.insert(user1);
        Task task=new Task(user0.getId(),"jeiw",10,10,1,0L,"dea","ejijfw","cc");
        Task task2=new Task(user0.getId(),"jeiw",10,10,1,System.currentTimeMillis()+2743892784327L,"dea","ejijfw","cc");
        taskMapper.insert(task);
        taskMapper.insert(task2);
        assert workController.partTask(new WorkVO(user1.getId(),task.getId(),WorkStatus.FINISH)).getCode()==6002;
        assert workController.partTask(new WorkVO(user1.getId(),task2.getId(), WorkStatus.TO_FINISH)).getCode()==4000;
        assert workController.partTask(new WorkVO(user1.getId(),task2.getId(), WorkStatus.TO_FINISH)).getCode()==6001;
//        workMapper.deleteByMap(null);
//        taskMapper.deleteByMap(null);
//        userMapper.deleteByMap(null);
    }
}
