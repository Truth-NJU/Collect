package com.collect.mapper;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class TaskPostProcessor {

    /**
     * 根据id删除或添加TaskPostProcessor
     */
    protected int id;

    /**
     * 名字
     */
    protected String name;

    /**
     * 描述
     */
    protected String desc;

    /**
     * 是否启用
     */
    protected boolean valid = true;

    /**
     * 获得其余属性
     */
    public Map<String, Object> getExtra() {
        return null;
    }

    /**
     * 根据其他属性更新processor
     *
     * @param extra, 更新必要的属性
     */
    public void update(Map<String, Object> extra) {
    }

    public void enable() {
        valid = true;
    }

    public void disable() {
        valid = false;
    }

    /**
     * 对任务进行打分，分数累加进score中
     *
     * @param tasks，任务id集合
     * @param userId,                          用户id
     * @param score，任务前一次处理的得分，初始为0，得分应该在0-1之间
     */
    abstract void process(List<Integer> tasks, int userId, Map<Integer, Double> score);

    //对策略描述及其他相关内容赋初始值
    abstract void init();

}
