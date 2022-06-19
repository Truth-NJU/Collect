package com.collect.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.collect.dto.DTO;
import com.collect.dto.WorkDTO;
import com.collect.po.enums.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
@Getter
@Setter
@TableName("work")
@AllArgsConstructor
@NoArgsConstructor
public class Work implements PO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("userId")
    private Integer userId;

    @TableField("taskId")
    private Integer taskId;

    @TableField("finish")
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
