package com.collect.service;

import com.collect.dto.AttributeDTO;
import com.collect.dto.ReportDTO;
import com.collect.dto.UserDTO;
import com.collect.mapper.*;
import com.collect.po.Task;
import com.collect.po.User;
import com.collect.po.Work;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.util.FileUtil;
import com.collect.util.ReportClusterUtil;
import com.collect.vo.TaskVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.*;


@SpringBootTest
public class ReportCluster {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TaskMapperWrapper taskMapperWrapper;

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    WorkMapper workMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    AttributeService attributeService;

    @Autowired
    UserService userService;

    @Autowired
    ReportClusterMapper reportClusterMapper;

    @Test
    public void normalTest(){
        List<ReportDTO> reportDTOList=new ArrayList<>();
        ReportDTO reportDTO1=new ReportDTO(0,0,0,"点击抽奖按钮三秒 点击停止抽奖 预期结果：转盘成功停止转动，抽奖成功 实际结果： 抽奖失败","","");
        reportDTO1.setPaths(Collections.singletonList("1.png"));
        reportDTOList.add(reportDTO1);
        ReportDTO reportDTO2=new ReportDTO(0,0,0,"点击“发现”按钮进入“发现”页面 点击“积分抽奖”按钮 点击“开始抽奖”按钮 点击“停止抽奖”按钮 无法出现抽奖结果，出现错误：很抱歉，抽奖失败，请检查网络[java.lang.lllegalStateException:Expected BEGIN_OBJECT but was STRING at line 1 column 2]","","");
        reportDTO2.setPaths(Collections.singletonList("2.png"));
        reportDTOList.add(reportDTO2);
        ReportDTO reportDTO3=new ReportDTO(0,0,0,"1.点击“我的”按钮 2.点击右上角设置图标 3.点击“个人信息”；4.点击“出生日期”选择2017.10.21 预期输出：步骤4后系统提示出生日期不正确，不合常理； 实际输出：能够正常的保存完善个人信息；","","");
        reportDTO3.setPaths(Collections.singletonList("3.jpg"));
        reportDTOList.add(reportDTO3);
        ReportDTO reportDTO4=new ReportDTO(0,0,0,"点击“任务广场”按钮 点击“1017-查询运动领域问法收集七”选项 点击“领取任务”按钮 点击“开始做任务” 未出现任务信息","","");
        reportDTO4.setPaths(Collections.singletonList("4.png"));
        reportDTOList.add(reportDTO4);
        ReportDTO reportDTO5=new ReportDTO(0,0,0,"进入任务 点击1017-查询运动领域问法收集 点击领取任务 点击开始做任务 直接提示没有更多题目。不管是1017-查询运动领域问法收集二，1017-查询运动领域问法收集六，还是其他几个相似题目，都没有可做题目，。此BUG严重等级一般，这几个任务复现程度是必现。影响用户体验。","","");
        reportDTO5.setPaths(Collections.singletonList("5.png"));
        reportDTOList.add(reportDTO5);
        ReportDTO reportDTO6=new ReportDTO(0,0,0,"进入“发现”页面 点击“寻找小伙伴”  需要点击寻找小伙伴才能找到“兴趣部落”按钮","","");
        reportDTO6.setPaths(Collections.singletonList("6.png"));
        reportDTOList.add(reportDTO6);
        ReportDTO reportDTO7=new ReportDTO(0,0,0,"进入兴趣部落 点击周贡献榜 信息未能正常显示","","");
        reportDTO7.setPaths(Collections.singletonList("7.jpg"));
        reportDTOList.add(reportDTO7);
        ReportDTO reportDTO8=new ReportDTO(0,0,0,"进入兴趣部落页面 点击“周贡献榜” 出现“网页无法打开”。","","");
        reportDTO8.setPaths(Collections.singletonList("8.jpg"));
        reportDTOList.add(reportDTO8);
        ReportDTO reportDTO9=new ReportDTO(0,0,0,"进入企鹅众测的兴趣部落 点击精华的标签，周贡献版等 显示无法打开页面","","");
        reportDTO9.setPaths(Collections.singletonList("9.jpg"));
        reportDTOList.add(reportDTO9);
        System.out.println(ReportClusterUtil.reportCluster(reportDTOList));

    }


    @Test
    public void finalTest() throws InterruptedException {
        UserDTO user0 = new UserDTO("1", "321", UserIdentity.DELIVER,"a");
        UserDTO user1 = new UserDTO("13@qq.com", "123456",  UserIdentity.WORKER,"dxy");
        UserDTO user2 = new UserDTO("23@qq.com", "123456",  UserIdentity.WORKER,"dxy");
        UserDTO user3 = new UserDTO("21@qq.com", "123456",  UserIdentity.WORKER,"dxy");
        UserDTO user4 = new UserDTO("3@qq.com", "123456",  UserIdentity.WORKER,"dxy");
        ArrayList<UserDTO> userList=new ArrayList<>();
        userList.add(user0);userList.add(user1);userList.add(user2);userList.add(user3);userList.add(user4);
        ArrayList<Integer> list=registerUser(userList);
        initUser(list);
        Task task = new Task(list.get(0), "软工三作业检查", 10, 10, 0, System.currentTimeMillis() + 2000, "a", "b", "fej");
        task.setDevice(DeviceType.Android);
        TaskVO taskVO=(TaskVO)(taskService.issueTask(task.toDTO()).getData().get(0));
        workMapper.insert(new Work(null, list.get(1), taskVO.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, list.get(2), taskVO.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, list.get(3), taskVO.getId(), WorkStatus.TO_FINISH));
        workMapper.insert(new Work(null, list.get(4), taskVO.getId(), WorkStatus.TO_FINISH));
        List<ReportDTO> reportDTOList=new ArrayList<>();
        ReportDTO reportDTO1=new ReportDTO(0,list.get(1),taskVO.getId(),"点击抽奖按钮三秒 点击停止抽奖 预期结果：转盘成功停止转动，抽奖成功 实际结果： 抽奖失败","","");
        reportDTO1.setPaths(Collections.singletonList("1.png"));
        reportDTOList.add(reportDTO1);
        ReportDTO reportDTO2=new ReportDTO(0,list.get(2),taskVO.getId(),"点击“发现”按钮进入“发现”页面 点击“积分抽奖”按钮 点击“开始抽奖”按钮 点击“停止抽奖”按钮 无法出现抽奖结果，出现错误：很抱歉，抽奖失败，请检查网络[java.lang.lllegalStateException:Expected BEGIN_OBJECT but was STRING at line 1 column 2]","","");
        reportDTO2.setPaths(Collections.singletonList("2.png"));
        reportDTOList.add(reportDTO2);
        ReportDTO reportDTO3=new ReportDTO(0,list.get(3),taskVO.getId(),"1.点击“我的”按钮 2.点击右上角设置图标 3.点击“个人信息”；4.点击“出生日期”选择2017.10.21 预期输出：步骤4后系统提示出生日期不正确，不合常理； 实际输出：能够正常的保存完善个人信息；","","");
        reportDTO3.setPaths(Collections.singletonList("3.jpg"));
        reportDTOList.add(reportDTO3);
        ReportDTO reportDTO4=new ReportDTO(0,list.get(4),taskVO.getId(),"点击“任务广场”按钮 点击“1017-查询运动领域问法收集七”选项 点击“领取任务”按钮 点击“开始做任务” 未出现任务信息","","");
        reportDTO4.setPaths(Collections.singletonList("4.png"));
        reportDTOList.add(reportDTO4);
        for (ReportDTO reportDTO : reportDTOList) {
            reportMapper.insert(reportDTO.toPO());
        }
        Thread.sleep(10000);
        HashMap<String,Object> map=new HashMap<>();
        map.put("taskId",taskVO.getId());
        assert reportClusterMapper.selectByMap(map).get(0).getClusterSet()!=null;
        Map<String,Object> map1=new HashMap<>();
        userMapper.deleteByMap(map1);
        FileUtil.heavyDelete("file"+ File.separator+"TaskClusters");
    }

    private ArrayList<Integer> registerUser(List<UserDTO> users){
        ArrayList<Integer> idList=new ArrayList<>();
        for (UserDTO user : users) {
            userService.register(user);
            String email=user.getEmail();
            HashMap<String,Object> map=new HashMap<>();
            map.put("email",email);
            User user1=userMapper.selectByMap(map).get(0);
            idList.add(user1.getId());
        }
        return idList;
    }

    private void initUser(List<Integer> idList){
        for (Integer userId : idList) {
            AttributeDTO attributeVO=new AttributeDTO();
            attributeVO.setUserId(userId);
            ArrayList<Integer> arrayList=new ArrayList<>();
            arrayList.add(0);arrayList.add(5);
            attributeVO.setDevice(arrayList);
            arrayList.clear();
            arrayList.add(0);arrayList.add(2);
            attributeVO.setPreferTask(arrayList);
            attributeService.init(attributeVO);
        }
    }

}
