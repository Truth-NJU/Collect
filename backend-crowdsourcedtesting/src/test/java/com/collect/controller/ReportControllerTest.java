package com.collect.controller;

import com.collect.dto.CommentDTO;
import com.collect.dto.ReportDTO;
import com.collect.mapper.*;
import com.collect.po.*;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.service.ReportService;
import com.collect.util.FileUtil;
import com.collect.util.reportStrategy.BadStrategy;
import com.collect.util.reportStrategy.SimilarStrategy;
import com.collect.vo.ReportVO;
import com.collect.vo.ResponseVO;
import com.collect.vo.VO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@SpringBootTest
@Component
@Transactional
public class ReportControllerTest {

    @Autowired
    SimilarStrategy similarStrategy;

    @Autowired
    BadStrategy badStrategy;

    @Autowired
    ReportService reportService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapper;

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    ReportImageMapper reportImageMapper;

    @Autowired
    WorkMapper workMapper;

    @Autowired
    CommentMapper commentMapper;

    @Test
    public void testCommitAndUpdateReport() {
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        User user0 = new User("1", "321", UserIdentity.DELIVER);
        User user1 = new User("12", "321", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        Task task = new Task(user0.getId(), "软工三作业检查", 10, 10, 0, System.currentTimeMillis() + 100000, "a", "b", "fej");
        taskMapper.insert(task);
        MultipartFile multipartFile = FileUtil.getMulFileByPath("src/test/java/com/collect/img/-4248c8a4e6883998.jpg");
        List<MultipartFile> images = new LinkedList<>();
        images.add(multipartFile);
        workMapper.insert(new Work(null, user1.getId(), task.getId(), WorkStatus.TO_FINISH));
        ReportDTO reportDTO = new ReportDTO(user1.getId(), task.getId(), images, "干得不错", "直接不错", "iPhone13 pro max 远峰蓝 1TB");
        reportService.commitReport(reportDTO);
        assert reportImageMapper.selectByMap(null).size()==1;
        ReportDTO reportDTO2 = new ReportDTO(user1.getId(), task.getId(), images, "干得不错", "直接不错", "iPhone13 pro max 远峰蓝 1TB");
        reportDTO2.setImages(new LinkedList<>());
        reportDTO2.setToKeep(Collections.singletonList(reportImageMapper.selectByMap(null).get(0).getImage()));
        reportDTO2.setNote("什么垃圾");
        reportDTO2.setSteps("上来就闪退");
        FileUtil.heavyDelete(FileUtil.REPORT_IMAGE);
        reportService.commitReport(reportDTO2);
        reportImageMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
        taskMapper.deleteById(task.getId());
        userMapper.deleteById(user0.getId());
        userMapper.deleteById(user1.getId());
    }

    @Test
    public void testLookReports() {
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        User user0 = new User("342", "tt321", UserIdentity.DELIVER);
        User user1 = new User("34", "321", UserIdentity.WORKER);
        User user2 = new User("34333", "321", UserIdentity.WORKER);
        userMapper.insert(user0);
        userMapper.insert(user1);
        userMapper.insert(user2);
        Task task = new Task(user0.getId(), "软工三作业检查", 10, 10, 0, 0L, "a", "b", "c");
        taskMapper.insert(task);
        workMapper.insert(new Work(null, user1.getId(), task.getId(), null));
        Report report1 = new Report(user1.getId(), task.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report1);
        workMapper.insert(new Work(null, user2.getId(), task.getId(), null));
        Report report2 = new Report(user2.getId(), task.getId(), "67大帝", "czy", "6+");
        reportMapper.insert(report2);
        List<String> list = Arrays.asList("123", "3423", "3213");
        for (String s : list) {
            reportImageMapper.insert(new ReportImage(report1.getId(), s));
        }
        List<String> list2 = Arrays.asList("123", "nimabi");
        for (String s : list2) {
            reportImageMapper.insert(new ReportImage(report2.getId(), s));
        }
        assert reportController.lookReports(task.getId()).getData().size() == 2;
        ReportVO reportVO = (ReportVO) reportController.lookReports(task.getId()).getData().get(0);
        assert reportVO.getPaths().size() == 3;
        reportVO = (ReportVO) reportController.lookReports(task.getId()).getData().get(1);
        assert reportVO.getPaths().size() == 2;
        reportImageMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
        taskMapper.deleteById(task.getId());
        userMapper.deleteById(user0.getId());
        userMapper.deleteById(user1.getId());
        userMapper.deleteById(user2.getId());
    }


    @Test
    public void testFindSimilarReportsFromSameTask(){
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        reportController.similarStrategy= similarStrategy;
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw",null,null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy",null,null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh",null,null);
        userMapper.insert(worker2);
        User worker3 = new User(null, "hxt@qq.com", "32312", null, UserIdentity.WORKER, "hxt",null,null);
        userMapper.insert(worker3);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report);
        // worker2领取任务
        Work job2 = new Work(null, worker2.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job2);
        // 在任务下发布报告
        Report report2 = new Report(null, worker2.getId(), task.getId(), "写得真好", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report2);
        // worker3领取任务
        Work job3 = new Work(null, worker3.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job3);
        // 在任务下发布报告
        Report report3 = new Report(null, worker3.getId(), task.getId(), "hxt写的真jb帅", "1.3.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report3);
        List<VO> similarReports=reportController.findSimilarReportsFromSameTask(report.getId()).getData();
        List<ReportDTO> similarreportDTOS=new ArrayList<>();
        for (VO vo : similarReports) {
            similarreportDTOS.add((ReportDTO) vo.toDTO());
        }
        assert similarreportDTOS.size()==2;

        userMapper.deleteByMap(null);
    }


    @Test
    public void testFindLowQualityReportsFromSameTask(){
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        reportController.badStrategy= badStrategy;
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw",null,null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy",null,null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh",null,null);
        userMapper.insert(worker2);
        User worker3 = new User(null, "hxt@qq.com", "32312", null, UserIdentity.WORKER, "hxt",null,null);
        userMapper.insert(worker3);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report);
        // worker2领取任务
        Work job2 = new Work(null, worker2.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job2);
        // 在任务下发布报告
        Report report2 = new Report(null, worker2.getId(), task.getId(), "写得真好", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report2);
        // worker3领取任务
        Work job3 = new Work(null, worker3.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job3);
        // 在任务下发布报告
        Report report3 = new Report(null, worker3.getId(), task.getId(), "hxt写的真jb帅", "1.3.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report3);
        List<VO> similarReports=reportController.findLowQualityReportsFromSameTask(report.getId()).getData();
        List<ReportDTO> similarreportDTOS=new ArrayList<>();
        for (VO vo : similarReports) {
            similarreportDTOS.add((ReportDTO) vo.toDTO());
        }
        assert similarreportDTOS.get(0).getStar()<=similarreportDTOS.get(1).getStar();
        assert similarreportDTOS.size()==2;

        userMapper.deleteByMap(null);
    }

    @Test
    public void testGiveMarkAndComment(){
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw",null,null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy",null,null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh",null,null);
        userMapper.insert(worker2);
        User worker3 = new User(null, "hxt@qq.com", "32312", null, UserIdentity.WORKER, "hxt",null,null);
        userMapper.insert(worker3);
        User worker4 = new User(null, "hxtttttt@qq.com", "32312", null, UserIdentity.WORKER, "hxtttttttt",null,null);
        userMapper.insert(worker4);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", null, null);
        reportMapper.insert(report);
        Comment comment1 = new Comment(null, report.getId(), worker1.getId(), 5.0, "这里有一个问题",null,null);
        Report newStar= (Report) reportController.giveMarkAndComment(comment1.toDTO().toVO()).getData().get(0).toDTO().toPO();
        assert newStar.getStar()==5.0;
        assert newStar.getStarNum()==1;
        Comment comment2 = new Comment(null, report.getId(), worker2.getId(), 4.0, "这里有两个问题",null,null);
        Report newStar2= (Report) reportController.giveMarkAndComment(comment2.toDTO().toVO()).getData().get(0).toDTO().toPO();
        assert newStar2.getStar()==4.5;
        assert newStar2.getStarNum()==2;
        Comment comment3 = new Comment(null, report.getId(), worker3.getId(), 3.0, "这里有三个问题",null,null);
        Report newStar3= (Report) reportController.giveMarkAndComment(comment3.toDTO().toVO()).getData().get(0).toDTO().toPO();
        assert newStar3.getStar()==4.0;
        assert newStar3.getStarNum()==3;

        userMapper.deleteByMap(null);
        taskMapper.deleteByMap(null);
        workMapper.deleteByMap(null);
        commentMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
    }


    @Test
    public void testGetCommentsUnderReport(){
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw",null,null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy",null,null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh",null,null);
        userMapper.insert(worker2);
        User worker3 = new User(null, "hxt@qq.com", "32312", null, UserIdentity.WORKER, "hxt",null,null);
        userMapper.insert(worker3);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report);
        // 在报告下面进行评论
        Comment comment1 = new Comment(null, report.getId(), worker1.getId(), 5.0, "这里有一个问题",null,null);
        commentMapper.insert(comment1);
        Comment comment2 = new Comment(null, report.getId(), worker2.getId(), 4.0, "这里有两个问题",null,null);
        commentMapper.insert(comment2);
        Comment comment3 = new Comment(null, report.getId(), worker3.getId(), 4.9, "这里有三个问题",null,null);
        commentMapper.insert(comment3);
        ResponseVO comments = reportController.getCommentsUnderReport(report.getId());
        List<VO> commentsVO = comments.getData();
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (VO vo : commentsVO) {
            commentDTOS.add((CommentDTO) vo.toDTO());
        }
        for (CommentDTO commentDTO : commentDTOS) {
            System.out.println(commentDTO.toString());
        }
        assert commentDTOS.get(0).getName().equals("dxy");
        assert commentDTOS.get(1).getName().equals("tzh");
        assert commentDTOS.get(2).getName().equals("hxt");

        userMapper.deleteByMap(null);
        taskMapper.deleteByMap(null);
        workMapper.deleteByMap(null);
        commentMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
    }

    @Autowired
    CoworkersMapper coworkersMapper;

    @Test
    public void testGiveAnnotation(){
        ReportController reportController = new ReportController();
        reportController.reportService = reportService;
        // 注册为发包方
        User user = new User(null, "czw@qq.com", "123456", null, UserIdentity.DELIVER, "czw",null,null);
        userMapper.insert(user);
        long time = System.currentTimeMillis() * 2;
        // 发布任务
        Task task = new Task(user.getId(), "SMTC", 123, 123, 0, time, "dsajdo", "fdejiodfj", "wo");
        taskMapper.insert(task);
        // 注册众包工人
        User worker1 = new User(null, "dxy@qq.com", "32312", null, UserIdentity.WORKER, "dxy",null,null);
        userMapper.insert(worker1);
        User worker2 = new User(null, "tzh@qq.com", "32312", null, UserIdentity.WORKER, "tzh",null,null);
        userMapper.insert(worker2);
        User worker3 = new User(null, "hxt@qq.com", "32312", null, UserIdentity.WORKER, "hxt",null,null);
        userMapper.insert(worker3);
        // worker1领取任务
        Work job = new Work(null, worker1.getId(), task.getId(), WorkStatus.TO_FINISH);
        workMapper.insert(job);
        // 在任务下发布报告
        Report report = new Report(null, worker1.getId(), task.getId(), "写得真好啊", "1.2.3.4.", "ios", 0.0, 0);
        reportMapper.insert(report);
        report.setNote("/ddiwojdoiwjdi%hdsiadioho%/dsdjsidojdioajdio%djiadjio%/");
        ReportVO reportVO=report.toDTO().toVO();
        reportVO.setAnnotationUserId(worker2.getId());
        reportController.giveAnnotation(reportVO);
        assert reportMapper.selectById(report.getId()).getNote().equals("/ddiwojdoiwjdi%hdsiadioho%/dsdjsidojdioajdio%djiadjio%/");

        userMapper.deleteByMap(null);
        taskMapper.deleteByMap(null);
        workMapper.deleteByMap(null);
        reportMapper.deleteByMap(null);
        coworkersMapper.deleteByMap(null);
    }
}

