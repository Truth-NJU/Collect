package com.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 小鱼丁
 * @since 2022-02-20
 */
@Repository
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
