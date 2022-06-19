package com.collect.service;

import com.collect.dto.TaskDTO;
import com.collect.vo.ResponseVO;
import com.collect.vo.TaskVO;
import com.collect.vo.VO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
public interface TaskService {

    ResponseVO updateTaskInfo(TaskDTO toDTO);

    ResponseVO findTaskByUserId(Integer userId);

    List<TaskVO> findTaskByTaskIdList(List<Integer> taskIdList);

    ResponseVO findAllTasks();

    ResponseVO issueTask(TaskDTO toDTO);

    ResponseVO selectTaskByLabel(Integer tag, Integer if_finished, String name);

    ResponseVO findTaskByTaskId(Integer userId, Integer taskId);

    TaskVO findTaskByTaskId(Integer taskId);

    ResponseVO recommendTaskByLabel(Integer tag, Integer if_finished, Integer userId);

    ResponseVO recommendAllTasks(Integer userId);

//    ResponseVO ifShown(Integer userId, Integer taskId);


}
