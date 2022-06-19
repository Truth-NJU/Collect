package com.collect.controller;


import com.collect.annotation.CheckLoginStatus;
import com.collect.mapper.WorkMapper;
import com.collect.po.enums.UserIdentity;
import com.collect.po.enums.WorkStatus;
import com.collect.service.WorkService;
import com.collect.vo.ResponseVO;
import com.collect.vo.TaskVO;
import com.collect.vo.WorkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/collect/work")
public class WorkController {

    @Autowired
    WorkService workService;


    /**
     * 众包工人参与任务
     *
     * @param workVO
     * @param
     * @return
     */
    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @PostMapping("/part")
    ResponseVO partTask(@RequestBody WorkVO workVO) {
        return workService.partTask(workVO.toDTO());
    }

    /**
     * 众包工人完成任务
     *
     * @param workVO
     * @return
     */
    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @PostMapping("/finish")
    ResponseVO finishTask(@RequestBody WorkVO workVO) {
        workVO.setFinish(WorkStatus.FINISH);
        return workService.finishTask(workVO.toDTO());
    }
}

