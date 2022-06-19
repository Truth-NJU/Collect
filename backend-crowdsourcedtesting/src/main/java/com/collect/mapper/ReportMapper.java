package com.collect.mapper;

import com.collect.po.Report;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
@Mapper
@Repository
public interface ReportMapper extends BaseMapper<Report> {
    Report selectLastByUserIdAndTaskId(int userId, int taskId);
}
