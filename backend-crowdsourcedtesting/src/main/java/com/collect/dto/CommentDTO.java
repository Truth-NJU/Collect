package com.collect.dto;


import com.collect.po.Comment;
import com.collect.po.PO;
import com.collect.vo.CommentVO;
import com.collect.vo.VO;
import lombok.*;

/**
 * 众包工人对报告的评分和评价
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDTO implements DTO {
    Integer id;

    Integer reportId;

    Integer userId;

    Double mark;

    String comment;

    String name;

    String email;
    @Override
    public Comment toPO() {
        return new Comment(id,reportId,userId,mark,comment,name,email);
    }

    @Override
    public CommentVO toVO() {
        return new CommentVO(id,reportId,userId,mark,comment,name,email);
    }
}
