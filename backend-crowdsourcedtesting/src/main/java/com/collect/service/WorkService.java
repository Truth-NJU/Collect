package com.collect.service;

import com.collect.dto.WorkDTO;
import com.collect.po.enums.WorkStatus;
import com.collect.vo.ResponseVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
public interface WorkService {

    ResponseVO findTaskByUserId(int userId, WorkStatus workStatus);

    ResponseVO partTask(WorkDTO workDTO);

    ResponseVO finishTask(WorkDTO workDTO);

    WorkStatus findWorkStatus(int userId, int taskId);

}
