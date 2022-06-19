package com.collect.controller;


import com.collect.annotation.CheckLoginStatus;
import com.collect.annotation.UserId;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.service.ReportService;
import com.collect.service.TaskService;
import com.collect.service.UserService;
import com.collect.service.WorkService;
import com.collect.vo.ResponseVO;
import com.collect.vo.TaskVO;
import com.collect.vo.VO;
import com.collect.vo.WorkVO;
import com.collect.vo.http.TaskHttpStatus;
import com.collect.vo.http.UserHttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/collect/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @Autowired
    WorkService workService;


    /**
     * 更新任务信息
     *
     * @param taskVO
     * @return
     */
    @CheckLoginStatus(identity = UserIdentity.DELIVER)
    @PostMapping("/update")
    ResponseVO updateTaskInfo(TaskVO taskVO) {
        return taskService.updateTaskInfo(taskVO.toDTO());
    }

    /**
     * 提交任务
     *
     * @param taskVO
     * @return
     */
    @CheckLoginStatus(identity = UserIdentity.DELIVER)
    @PostMapping("/issue")
    ResponseVO issueTask(TaskVO taskVO) {
        return taskService.issueTask(taskVO.toDTO());
    }

    /**
     * 发包方看到自己的发布任务
     *
     * @param userId
     * @return
     */
    @GetMapping("/searchPublishedTask")
    ResponseVO searchPublishedTask(@RequestParam @UserId Integer userId) {
        if (userService.getIdentityByUserId(userId) == null) {
            return ResponseVO.fail(UserHttpStatus.USER_NOT_EXIST);
        }
        // 当前用户不是发包方
        else if (userService.getIdentityByUserId(userId) != UserIdentity.DELIVER) {
            return ResponseVO.fail(TaskHttpStatus.TASK_Identity_Error);
        } else {
            return taskService.findTaskByUserId(userId);
        }
    }

    /**
     * 众包工人查看自己的工作
     *
     * @param userId
     * @return
     */
    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @GetMapping("/searchFinishedTask")
    ResponseVO searchTaskForWorker(@RequestParam @UserId Integer userId, WorkStatus workStatus) {
        return workService.findTaskByUserId(userId, workStatus);
    }

    /**
     * 查看所有任务
     */
    @GetMapping("/allTask")
    ResponseVO findAllTasks() {
        return taskService.findAllTasks();
    }

    /**
     * 使用推荐算法查看所有任务
     */
    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @GetMapping("/recommendTask")
    ResponseVO recommendAllTasks(@UserId Integer userId) {
        return taskService.recommendAllTasks(userId);
    }

    /**
     * 根据标签选择任务，标签包括tag和是否完成，0代表未结束，1代表已结束
     */
    @GetMapping("/selectTaskByLabel")
    ResponseVO selectTaskByLabel(Integer tag, Integer if_finished, @RequestParam String name) {
        return taskService.selectTaskByLabel(tag, if_finished, name);
    }

    /**
     * 根据标签使用推荐算法查看所有任务，标签包括tag和是否完成，0代表未结束，1代表已结束
     */
    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @GetMapping("/recommendTaskByLabel")
    ResponseVO recommendTaskByLabel(Integer tag, Integer if_finished, @UserId Integer userId) {
        return taskService.recommendTaskByLabel(tag, if_finished, userId);
    }

    /**
     * 查看任务详情，如果userId = task的userId，则返回全部信息
     */
    //@CheckLoginStatus(identity = UserIdentity.DELIVER)
    @GetMapping("/info")
    ResponseVO findTaskById(@RequestParam Integer userId, @RequestParam Integer taskId) {
        return taskService.findTaskByTaskId(userId, taskId);
    }

    /**
     * 众包工人查看任务详情，判断是否参加任务
     *
     * @param userId
     * @param taskId
     * @return
     */
    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @GetMapping("/ifPart")
    ResponseVO ifWork(@RequestParam @UserId Integer userId, @RequestParam Integer taskId) {
        // 判断用户是否参加该任务
        WorkStatus workStatus = workService.findWorkStatus(userId, taskId);
        // 若没参加，则返回任务脱敏信息，否则返回详细信息
        TaskVO taskVO = workStatus == null ? (TaskVO) taskService.findTaskByTaskId(userId, taskId).getData().get(0) : taskService.findTaskByTaskId(taskId);
        if (taskVO == null) {
            return ResponseVO.fail(TaskHttpStatus.TASK_NOT_EXIST);
        }
        taskVO.setWorkStatus(workStatus);
        return ResponseVO.succeed(taskVO);
    }

}

