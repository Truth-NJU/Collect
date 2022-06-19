package com.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.po.ReportCluster;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface ReportClusterMapper extends BaseMapper<ReportCluster> {
    String getClusterSet(int taskId);
}
