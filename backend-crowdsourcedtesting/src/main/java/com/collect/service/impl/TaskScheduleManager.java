package com.collect.service.impl;

import com.collect.dto.ReportDTO;
import com.collect.mapper.*;
import com.collect.po.*;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.PreferTask;
import com.collect.po.enums.WorkStatus;
import com.collect.service.ReportService;
import com.collect.util.ReportClusterUtil;
import com.collect.vo.VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskScheduleManager {

    private final ThreadPoolTaskScheduler scheduler;

    private final WorkMapper workMapper;

    private final AttributeMapper attributeMapper;

    private final ReportMapper reportMapper;

    private final ReportClusterMapper reportClusterMapper;

    private final Map<Integer, ScheduleTask> idTaskMap = new ConcurrentHashMap<>();

    private final ReportService reportService;


    @Autowired
    public TaskScheduleManager(@Lazy ThreadPoolTaskScheduler scheduler, WorkMapper workMapper, AttributeMapper attributeMapper, ReportMapper reportMapper,ReportServiceImpl reportService,ReportClusterMapper reportClusterMapper) {
        this.scheduler = scheduler;
        this.workMapper = workMapper;
        this.attributeMapper = attributeMapper;
        this.reportMapper = reportMapper;
        this.reportService=reportService;
        this.reportClusterMapper=reportClusterMapper;
    }

    @Bean
    private ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 开启十个线程
        scheduler.setPoolSize(10);
        return scheduler;
    }

    public void submit(Task task) {
        ScheduleTask scheduleTask = new ScheduleTask(task);
        idTaskMap.put(scheduleTask.taskId, scheduleTask);
        scheduler.schedule(scheduleTask, scheduleTask.executeTime);
    }

    public void cancel(int taskId) {
        ScheduleTask toCancel = idTaskMap.remove(taskId);
        if (toCancel != null) {
            toCancel.cancel();
        }
    }

    public void update(Task task) {
        cancel(task.getId());
        submit(task);
    }

    class ScheduleTask implements Runnable {

        int taskId;

        Date executeTime;

        boolean isCanceled;

        PreferTask preferTask;

        DeviceType deviceType;



        ScheduleTask(Task task) {
            this.taskId = task.getId();
            this.executeTime = new Date(task.getDate());
            this.isCanceled = false;
            this.preferTask = PreferTask.values()[task.getTag()];
            this.deviceType = DeviceType.values()[task.getDevice().getCode()];
        }

        void cancel() {
            this.isCanceled = true;
        }

        @Override
        public void run() {
            if (!isCanceled) {
                Map<String, Object> selectByTaskId = new HashMap<>();
                Map<String, Object> selectByUserId = new HashMap<>();
                Map<String, Object> selectByTaskIdAndUserId = new HashMap<>();
                selectByTaskId.put("taskId", this.taskId);
                selectByTaskIdAndUserId.put("taskId", this.taskId);
                // 获得所有参与该任务的工人
                List<Work> workList = workMapper.selectByMap(selectByTaskId);
                for (Work worker : workList) {
                    int userId = worker.getUserId();
                    selectByUserId.put("userId", userId);
                    selectByTaskIdAndUserId.put("userId", userId);
                    // 查看工人是否提交报告
                    List<Report> reports = reportMapper.selectByMap(selectByTaskIdAndUserId);
                    if (reports == null || reports.size() == 0) {
                        // 若未提交报告，设置工作状态为失败
                        worker.setFinish(WorkStatus.FAIL);
                        workMapper.updateById(worker);
                    } else if (worker.getFinish().equals(WorkStatus.TO_FINISH)) {
                        // 若提交报告且工作未主动完成，则设置为完成
                        worker.setFinish(WorkStatus.FINISH);
                        workMapper.updateById(worker);
                    }
                    // 获得工人属性，根据任务类型和报告评分更改工人属性
                    Attribute attribute = attributeMapper.selectByMap(selectByUserId).get(0);
                    // 计算报告平均得分，若未参加任务，则为0
                    double averageScore = 0.0;
                    if (reports != null && reports.size() != 0) {
                        for (Report report : reports) {
                            averageScore += report.getStar();
                        }
                        averageScore /= reports.size();
                    }
                    // 计算任务类型能力和偏好
                    switch (this.preferTask) {
                        case BUG:
                            attribute.setBugAbility(attribute.getBugAbility() + averageScore);
                            attribute.setBug(attribute.getBug() + 1);
                            break;
                        case FUNCTION:
                            attribute.setFunctionalAbility(attribute.getFunctionalAbility() + averageScore);
                            attribute.setFunctional(attribute.getFunctional() + 1);
                            break;
                        case PERFORMANCE:
                            attribute.setPerformanceAbility(attribute.getPerformanceAbility() + averageScore);
                            attribute.setPerformance(attribute.getPerformance() + 1);
                            break;
                    }
                    // 计算设备类型偏好
                    switch (this.deviceType) {
                        case Windows:
                            attribute.setWindows(attribute.getWindows() + 1);
                            break;
                        case Linux:
                            attribute.setLinux(attribute.getLinux() + 1);
                            break;
                        case MacOS:
                            attribute.setMac(attribute.getMac() + 1);
                            break;
                        case IOS:
                            attribute.setIos(attribute.getIos() + 1);
                            break;
                        case Android:
                            attribute.setAndroid(attribute.getAndroid() + 1);
                            break;
                        case HarmonyOS:
                            attribute.setHarmonyos(attribute.getHarmonyos() + 1);
                            break;
                    }
                    attributeMapper.updateById(attribute);
                    // TODO 计算信誉？


                }
                // 从map中删除该任务
                idTaskMap.remove(taskId);


                List<VO> temp=reportService.lookReports(taskId).getData();
                List<ReportDTO> reportList=new ArrayList<>();
                for (VO vo : temp) {
                    reportList.add((ReportDTO) vo.toDTO());
                }
                List<List> ret=ReportClusterUtil.reportCluster(reportList);
                List<Set<Integer>> clusterAns= ret.get(0);
                List<String> paths=ret.get(1);
                ReportCluster reportCluster =new ReportCluster();
                StringBuilder temp1= new StringBuilder();
                System.out.println(clusterAns);
                int i=0;
                for (Set<Integer> cluster : clusterAns) {
                    temp1.append('#');
                    for (Integer integer : cluster) {
                        temp1.append(reportList.get(integer).getId()).append(" ");
                    }
                    temp1.append('^').append(paths.get(i++)).append('#');
                }
                reportCluster.setTaskId(taskId);
                reportCluster.setClusterSet(temp1.toString());
                System.out.println(reportCluster);
                reportClusterMapper.insert(reportCluster);

            }
        }
    }
}
