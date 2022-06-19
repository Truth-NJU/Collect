package com.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.collect.po.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> selectCommentByReportId(Integer reportId);

    List<Comment> selectComment(Integer reportId, Integer userId);
}
