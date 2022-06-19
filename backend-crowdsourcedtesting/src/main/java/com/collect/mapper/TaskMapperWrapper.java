package com.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.config.TaskConfig;
import com.collect.exception.MyException;
import com.collect.po.Task;
import com.collect.vo.RuleVO;
import com.collect.vo.VO;
import com.collect.vo.http.RuleHttpStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TaskMapperWrapper {

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TaskConfig taskConfig;

    @Autowired
    private List<TaskPostProcessor> postProcessors;

    public List<Task> selectByMap(Map<String, Object> map) {
        return taskMapper.selectByMap(map);
    }

    public void deleteByMap(Map<String, Object> map) {
        taskMapper.deleteByMap(map);
    }

    public int insert(Task task) {
        return taskMapper.insert(task);
    }

    public int updateById(Task task) {
        return taskMapper.updateById(task);
    }

    public Task selectById(Integer id) {
        return taskMapper.selectById(id);
    }

    public void deleteById(Integer id) {
        taskMapper.deleteById(id);
    }

    public List<Task> selectTaskByLabel(Integer tag, Integer if_finished, String name, long currentTimeMillis) {
        return taskMapper.selectTaskByLabel(tag, if_finished, name, currentTimeMillis);
    }

    public List<Task> recommendAll(int userId) {
        List<Integer> tasks = taskMapper.recommendAll(taskConfig.getLimit());
        return post(tasks, userId);
    }

    public List<Task> recommendTaskByLabel(Integer tag, Integer if_finished, long now_time, int userId) {
        List<Integer> tasks = taskMapper.recommendByLabel(tag, if_finished, now_time, taskConfig.getLimit());
        return post(tasks, userId);
    }

    private List<Task> post(List<Integer> tasks, int userId) {
        if (tasks == null || tasks.size() == 0) {
            return new ArrayList<>();
        }
        Map<Integer, Double> score = new HashMap<>(2048);
        for (Integer id : tasks) {
            score.put(id, 0.0);
        }
        for (TaskPostProcessor processor : postProcessors) {
            if (processor.isValid()) {
                processor.process(tasks, userId, score);
            }
        }
        // 倒序返回
        tasks.sort((a, b) -> score.get(b).compareTo(score.get(a)));
        List<Task> res = new ArrayList<>();
        for (int i = 0; i < Math.min(tasks.size(), taskConfig.getFilter()); i++) {
            res.add(taskMapper.selectByIdWorkView(tasks.get(i)));
        }
//        taskMapper.selectInIdList(tasks);
//        res.sort(Comparator.comparingDouble(t -> -score.get(t.getId())));
        return res;
    }

    public synchronized void changeProcessor(List<RuleVO> ruleVOS) throws MyException {
        LinkedHashSet<RuleVO> idSet = new LinkedHashSet<>(ruleVOS);
        List<TaskPostProcessor> copy = new CopyOnWriteArrayList<>(postProcessors);
        int first = 0;
        // 调整processors顺序，插入排序
        for (RuleVO ruleVO : idSet) {
            int idx = 0;
            for (; idx < postProcessors.size(); idx++) {
                if (postProcessors.get(idx).getId() == ruleVO.getId()) {
                    if (first != idx) {
                        TaskPostProcessor temp = postProcessors.get(first);
                        postProcessors.set(first, postProcessors.get(idx));
                        postProcessors.set(idx, temp);
                    }
                    postProcessors.get(first).enable();
                    if (ruleVO.getExtra() != null) {
                        postProcessors.get(first).update(ruleVO.getExtra());
                    }
                    break;
                }
            }
            first++;
            if (idx == postProcessors.size()) {
                // 恢复到原有状态，并抛出异常
                postProcessors = copy;
                throw MyException.of(RuleHttpStatus.RULE_NOT_IN);
            }
        }
        while (first < postProcessors.size()) {
            postProcessors.get(first).disable();
            first++;
        }
    }

    /**
     * 返回所有postProcessors信息
     *
     * @return
     */
    public List<VO> findCurrentRule() {
        List<VO> res = new ArrayList<>();
        for (TaskPostProcessor processor : postProcessors) {
            RuleVO ruleVO = new RuleVO();
            ruleVO.setName(processor.getName());
            ruleVO.setHint(processor.getDesc());
            ruleVO.setId(processor.getId());
            ruleVO.setValid(processor.isValid());
            ruleVO.setExtra(processor.getExtra());
            res.add(ruleVO);
        }
        return res;
    }

    @Mapper
    @Repository
    public interface TaskMapper extends BaseMapper<Task> {

        List<Task> selectTaskByLabel(Integer tag, Integer if_finished, String name, long now_time);

        List<Integer> recommendAll(int limit);

        List<Integer> recommendByLabel(Integer tag, Integer if_finished, long now_time, int limit);

//        List<Task> selectInIdList(List<Integer> ids);

        Task selectByIdWorkView(int taskId);
    }

}
