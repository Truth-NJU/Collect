package com.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.po.UserWorkList;
import com.collect.po.Work;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
public interface WorkMapper extends BaseMapper<Work> {

    List<UserWorkList> findUsersTaskPartIn(List<Integer> taskIds);

    List<Integer> selectPartTasksByUserId(int userId);

}
