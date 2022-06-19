package com.collect.vo;

import com.collect.annotation.UserId;
import com.collect.dto.WorkDTO;
import com.collect.po.enums.WorkStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkVO implements VO{

    @UserId
    private Integer userId;

    private Integer taskId;

    private WorkStatus finish;

    @Override
    public WorkDTO toDTO() {
        WorkDTO workDTO = new WorkDTO();
        workDTO.setUserId(userId);
        workDTO.setTaskId(taskId);
        workDTO.setFinish(finish);
        return workDTO;
    }
}
