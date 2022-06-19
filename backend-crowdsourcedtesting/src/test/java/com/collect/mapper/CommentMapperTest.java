package com.collect.mapper;

import com.collect.CollectApplication;
import com.collect.po.Report;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.Work;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.collect.po.Comment;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest(classes = CollectApplication.class)
public class CommentMapperTest {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    WorkMapper workMapper;

    /**
     * 测试根据报告id查询报告下评论
     */
    @Test
    public void testSelectCommentByReportId() {
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw", null, null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy", null, null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh", null, null);
        userMapper.insert(worker2);
        User worker3 = new User(null, "hxt@qq.com", "32312", null, UserIdentity.WORKER, "hxt", null, null);
        userMapper.insert(worker3);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report);
        // 在报告下面进行评论
        Comment comment1 = new Comment(null, report.getId(), worker1.getId(), 5.0, "这里有一个问题", null, null);
        commentMapper.insert(comment1);
        Comment comment2 = new Comment(null, report.getId(), worker2.getId(), 4.0, "这里有两个问题", null, null);
        commentMapper.insert(comment2);
        Comment comment3 = new Comment(null, report.getId(), worker3.getId(), 4.9, "这里有三个问题", null, null);
        commentMapper.insert(comment3);
        List<Comment> comments = commentMapper.selectCommentByReportId(report.getId());
        assert comments.get(0).getName().equals("dxy");
        assert comments.get(1).getName().equals("tzh");
        assert comments.get(2).getName().equals("hxt");

        userMapper.deleteByMap(null);
        taskMapper.deleteByMap(null);
        workMapper.deleteByMap(null);
        commentMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
    }

    /**
     * 测试查询报告下用户发表的comments
     */
    @Test
    @Transactional
    public void testSelectComment() {
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw", null, null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy", null, null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh", null, null);
        userMapper.insert(worker2);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report);
        // 在报告下面进行评论
        Comment comment1 = new Comment(null, report.getId(), worker1.getId(), 5.0, "这里有一个问题", null, null);
        commentMapper.insert(comment1);
        Comment comment2 = new Comment(null, report.getId(), worker2.getId(), 4.0, "这里有两个问题", null, null);
        commentMapper.insert(comment2);
        List<Comment> comments = commentMapper.selectComment(report.getId(), worker1.getId());
        assert comments.size() == 1 && comments.get(0).getId().equals(comment1.getId());
        comments = commentMapper.selectComment(report.getId(), worker2.getId());
        assert comments.size() == 1 && comments.get(0).getId().equals(comment2.getId());
    }
}
