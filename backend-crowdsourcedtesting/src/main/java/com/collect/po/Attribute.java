package com.collect.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.collect.annotation.Calculate;
import com.collect.dto.AttributeDTO;
import com.collect.dto.DTO;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.PreferTask;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("attribute")
public class Attribute implements PO {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("userId")
    private Integer userId;

    @Calculate(desc = "windows平台参与")
    @TableField("windows")
    private Integer windows;

    @Calculate(desc = "mac平台参与")
    @TableField("mac")
    private Integer mac;

    @Calculate(desc = "android平台参与")
    @TableField("android")
    private Integer android;

    @Calculate(desc = "ios平台参与")
    @TableField("ios")
    private Integer ios;

    @Calculate(desc = "harmony平台参与")
    @TableField("harmony")
    private Integer harmonyos;

    @Calculate(desc = "linux平台参与")
    @TableField("linux")
    private Integer linux;

    @Calculate(desc = "性能任务参与")
    @TableField("performance")
    private Integer performance;

    @Calculate(desc = "功能任务参与")
    @TableField("functional")
    private Integer functional;

    @Calculate(desc = "稳定性任务参与")
    @TableField("bug")
    private Integer bug;

    @Calculate(mul = 0.2, desc = "性能任务能力")
    @TableField("performanceAbility")
    private Double performanceAbility;

    @Calculate(mul = 0.2, desc = "稳定性任务能力")
    @TableField("bugAbility")
    private Double bugAbility;

    @Calculate(mul = 0.2, desc = "功能任务能力")
    @TableField("functionalAbility")
    private Double functionalAbility;

    @Calculate(desc = "用户信誉")
    @TableField("credibility")
    private Double credibility;

    @Override
    public DTO toDTO() {
        return null;
    }

    public Integer getSum() {
        return functional + bug + performance;
    }
}
