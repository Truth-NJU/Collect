package com.collect.service.impl;

import com.collect.dto.TaskDTO;
import com.collect.mapper.TaskMapperWrapper;
import com.collect.po.Task;
import com.collect.po.enums.DeviceType;
import com.collect.service.TaskService;
import com.collect.util.DataCheck;
import com.collect.util.FileUtil;
import com.collect.vo.ResponseVO;
import com.collect.vo.TaskVO;
import com.collect.vo.VO;
import com.collect.vo.http.TaskHttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskScheduleManager scheduleManager;

    private final TaskMapperWrapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskScheduleManager scheduleManager, TaskMapperWrapper taskMapper) {
        this.scheduleManager = scheduleManager;
        this.taskMapper = taskMapper;
    }

    /**
     * @param taskDTO 发布任务
     * @return taskVO
     */
    @Override
    public ResponseVO issueTask(TaskDTO taskDTO) {
        //参与人数不能大于10000
        if (DataCheck.INTCheck(taskDTO.getNumber(), 10000)) {
            return ResponseVO.fail(TaskHttpStatus.TASK_PARTNUMBER_ERROR);
        }
        //截止日期不能早于现在
        if (DataCheck.DateCheck(taskDTO.getDate())) {
            return ResponseVO.fail(TaskHttpStatus.TASK_DATE_ERROR);
        }
        //设置remain=number
        taskDTO.setRemain(taskDTO.getNumber());
        //插入新task，并获得id
        Task task = taskDTO.toPO();
        taskMapper.insert(task);
        // 加入定时任务
        scheduleManager.submit(task);

        DeviceType deviceType = DeviceType.values()[task.getDevice().getCode()];
        //如果上传了apk文件，根据id获得路径并且将文件存入
        if (taskDTO.getApk() != null) {
            if (!FileUtil.checkFileType(taskDTO.getApk(), DeviceType.getSuffix(deviceType))) {
                return ResponseVO.fail(TaskHttpStatus.TASK_FILE_EXT_ERROR);
            }
            task.setAurl(FileUtil.save(taskDTO.getApk(), FileUtil.TASK_PUBLISH, task.getId()));
        }
        //如果上传了pdf文件，根据id获得路径并且将文件存入
        if (taskDTO.getPdf() != null) {
            if (!FileUtil.checkFileType(taskDTO.getPdf(), "pdf", "doc", "docx", "md", "txt", "png", "jpg", "jpeg")) {
                return ResponseVO.fail(TaskHttpStatus.TASK_FILE_EXT_ERROR);
            }
            task.setPurl(FileUtil.save(taskDTO.getPdf(), FileUtil.TASK_DESC, task.getId()));
        }
        //更新aurl和purl
        if (task.getAurl() != null || task.getPurl() != null)
            taskMapper.updateById(task);
        return ResponseVO.succeed(task.toDTO().toVO());
    }


    /**
     * @param taskDTO 更新任务
     * @return taskVO
     */
    @Override
    public ResponseVO updateTaskInfo(TaskDTO taskDTO) {
        // 参与人数不能大于10000
        if (DataCheck.INTCheck(taskDTO.getNumber(), 10000)) {
            return ResponseVO.fail(TaskHttpStatus.TASK_PARTNUMBER_ERROR);
        }
        // 截止日期不能早于现在
        if (DataCheck.DateCheck(taskDTO.getDate())) {
            return ResponseVO.fail(TaskHttpStatus.TASK_DATE_ERROR);
        }
        // 根据taskid和userid查询该任务是否存在，是否属于该发包者
        Task oldTask = taskMapper.selectById(taskDTO.getId());
        if (oldTask == null || !oldTask.getUserId().equals(taskDTO.getUserId())) {
            return ResponseVO.fail(TaskHttpStatus.TASK_NOT_OWN_PRIVILEGE);
        }
        taskDTO.setPurl(oldTask.getPurl());
        taskDTO.setAurl(oldTask.getAurl());
        if (DataCheck.DateCheck(oldTask.getDate())) {
            return ResponseVO.fail(TaskHttpStatus.TASK_HAS_FINISHED);
        }

        //根据新的number更新remain，number不能使remain低于0
        taskDTO.setRemain(oldTask.getRemain() - oldTask.getNumber() + taskDTO.getNumber());
        if (taskDTO.getRemain() < 0) {
            return ResponseVO.fail(TaskHttpStatus.TASK_PARTNUMBER_ERROR);
        }

        // 开始更新任务
        int taskId = taskDTO.getId();

        //如果要删除apk文件或者更新apk文件
        if (taskDTO.getDeleteApk() || taskDTO.getApk() != null) {
            taskDTO.setAurl(null);
            if (taskDTO.getApk() != null) {
                DeviceType deviceType = DeviceType.values()[taskDTO.getDevice().getCode()];
                if (!FileUtil.checkFileType(taskDTO.getApk(), DeviceType.getSuffix(deviceType))) {
                    return ResponseVO.fail(TaskHttpStatus.TASK_FILE_EXT_ERROR);
                }
                taskDTO.setAurl(FileUtil.save(taskDTO.getApk(), FileUtil.TASK_PUBLISH, taskDTO.getId()));
            }
            if (!FileUtil.delete(FileUtil.TASK_PUBLISH, oldTask.getAurl())) {
                return ResponseVO.fail(TaskHttpStatus.TASK_DELETE_FAIL);
            }
        }
        //如果要删除pdf文件或者更新pdf文件
        if (taskDTO.getDeletePdf() || taskDTO.getPdf() != null) {
            taskDTO.setPurl(null);
            if (taskDTO.getPdf() != null) {
                if (!FileUtil.checkFileType(taskDTO.getPdf(), "pdf", "doc", "docx", "md", "txt", "png", "jpg", "jpeg")) {
                    return ResponseVO.fail(TaskHttpStatus.TASK_FILE_EXT_ERROR);
                }
                taskDTO.setPurl(FileUtil.save(taskDTO.getPdf(), FileUtil.TASK_DESC, taskId));
            }
            if (!FileUtil.delete(FileUtil.TASK_DESC, oldTask.getPurl())) {
                return ResponseVO.fail(TaskHttpStatus.TASK_DELETE_FAIL);
            }
        }
        //更新task
        Task newTask = taskDTO.toPO();
        taskMapper.updateById(newTask);
        // 更新定时任务
        scheduleManager.update(newTask);
        return ResponseVO.succeed(taskDTO.toVO());
    }

    /**
     * 根据发布者id查找发布者发布的所有任务
     * 是发包者的功能
     *
     * @param userId，发包者id
     * @return 发包方的全部任务的详细信息
     */
    @Override
    public ResponseVO findTaskByUserId(Integer userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<Task> taskList = taskMapper.selectByMap(map);
        List<VO> taskVOS = new ArrayList<>();
        for (Task task : taskList) {
            taskVOS.add(task.toDTO().toVO());
        }
        return ResponseVO.succeed(taskVOS);
    }

    /**
     * 根据任务id列表获得任务内容
     *
     * @param taskIdList
     * @return
     */
    @Override
    public List<TaskVO> findTaskByTaskIdList(List<Integer> taskIdList) {
        List<Task> taskList = new ArrayList<>();
        for (Integer taskId : taskIdList) {
            // 根据taskId获得task内容，放入列表
            Task task = taskMapper.selectById(taskId);
            taskList.add(task);
        }
        if (taskList.size() == 0) {
            return new ArrayList<>();
        } else {
            List<TaskVO> taskVOS = new ArrayList<>();
            for (Task task : taskList) {
                taskVOS.add(task.toDTO().toVO());
            }
            return taskVOS;
        }
    }

    /**
     * ，
     * 返回全部任务
     *
     * @return 全部任务信息，去除敏感数据
     */
    @Override
    public ResponseVO findAllTasks() {
        List<Task> tasks = taskMapper.selectByMap(null);
        List<VO> t = new ArrayList<>();
        for (Task task : tasks) {
            task.setAurl(null);
            task.setPurl(null);
            t.add(task.toDTO().toVO());
        }
        return ResponseVO.succeed(t);
    }

    /**
     * 返回符合特征的全部任务
     *
     * @param tag，任务类型
     * @param if_finished，任务是否结束
     * @param name，任务名称
     * @return 符合特征的所有任务信息，去除敏感数据
     */
    @Override
    public ResponseVO selectTaskByLabel(Integer tag, Integer if_finished, String name) {
        List<Task> tasks = taskMapper.selectTaskByLabel(tag, if_finished, name, System.currentTimeMillis());
        List<VO> t = new ArrayList<>();
        for (Task task : tasks) {
            task.setAurl(null);
            task.setPurl(null);
            t.add(task.toDTO().toVO());
        }
        return ResponseVO.succeed(t);
    }

    @Override
    public ResponseVO recommendAllTasks(Integer userId) {
        List<Task> recommendList = taskMapper.recommendAll(userId);
        List<VO> res = new ArrayList<>(recommendList.size());
        recommendList.forEach(task -> res.add(task.toDTO().toVO()));
        return ResponseVO.succeed(res);
    }

    @Override
    public ResponseVO recommendTaskByLabel(Integer tag, Integer if_finished, Integer userId) {
        List<Task> recommendList = taskMapper.recommendTaskByLabel(tag, if_finished, System.currentTimeMillis(), userId);
        List<VO> res = new ArrayList<>(recommendList.size());
        recommendList.forEach(task -> res.add(task.toDTO().toVO()));
        return ResponseVO.succeed(res);
    }

    /**
     * 根据用户id和taskId查询任务详细信息，
     * 若用户为发包方且持有该任务，则返回详细信息
     *
     * @param userId，用户id
     * @param taskId，任务id
     * @return 任务详细信息
     */
    @Override
    public ResponseVO findTaskByTaskId(Integer userId, Integer taskId) {
        TaskVO task = findTaskByTaskId(taskId);
        if (task == null) {
            return ResponseVO.fail(TaskHttpStatus.TASK_NOT_EXIST);
        }
        if (!task.getUserId().equals(userId)) {
            task.setAurl(null);
            task.setPurl(null);
        }
        return ResponseVO.succeed(task);
    }

    /**
     * 根据taskId查询单个任务的详细信息
     *
     * @param taskId，任务id
     * @return 任务详细信息
     */
    @Override
    public TaskVO findTaskByTaskId(Integer taskId) {
        Task task = taskMapper.selectById(taskId);
        return task == null ? null : task.toDTO().toVO();
    }

}
