package com.collect.dto;

import com.collect.po.Work;
import com.collect.po.enums.WorkStatus;
import com.collect.vo.WorkVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkDTO implements DTO {

    private Integer userId;

    private Integer taskId;

    private WorkStatus finish;

    public WorkDTO(Integer userId,Integer taskId){
        this(userId,taskId,null);
    }

    @Override
    public Work toPO() {
        Work work = new Work();
        work.setUserId(userId);
        work.setTaskId(taskId);
        work.setFinish(finish);
        return work;
    }

    @Override
    public WorkVO toVO() {
        WorkVO work = new WorkVO();
        work.setUserId(userId);
        work.setTaskId(taskId);
        work.setFinish(finish);
        return work;
    }
}
