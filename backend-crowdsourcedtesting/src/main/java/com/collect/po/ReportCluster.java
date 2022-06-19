package com.collect.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("report_cluster")
public class ReportCluster {

    @TableField("taskId")
    private Integer taskId;

    @TableField("cluster_set")
    private String clusterSet;
}
