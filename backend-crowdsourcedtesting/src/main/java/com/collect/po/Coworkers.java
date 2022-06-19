package com.collect.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("coworkers")
public class Coworkers {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("workId")
    private Integer workId;

    @TableField("coworkerId")
    private Integer coworkerId;
}
