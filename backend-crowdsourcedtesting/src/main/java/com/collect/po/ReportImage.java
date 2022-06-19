package com.collect.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@AllArgsConstructor
@NoArgsConstructor
@TableName("report_image")
public class ReportImage{

    @TableField("reportId")
    private Integer reportId;

    @TableField("image")
    private String image;

}
