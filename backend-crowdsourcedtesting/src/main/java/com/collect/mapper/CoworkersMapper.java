package com.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.po.Coworkers;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CoworkersMapper extends BaseMapper<Coworkers> {
}
