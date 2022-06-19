package com.collect.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.collect.dto.CommentDTO;
import com.collect.dto.DTO;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("comment")
public class Comment implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    Integer id;

    @TableField("reportId")
    Integer reportId;

    @TableField("userId")
    Integer userId;

    @TableField("mark")
    Double mark;

    @TableField("comment")
    String comment;

    String name;

    String email;

    @Override
    public CommentDTO toDTO() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setReportId(reportId);
        commentDTO.setUserId(userId);
        commentDTO.setMark(mark);
        commentDTO.setComment(comment);
        commentDTO.setName(name);
        commentDTO.setEmail(email);
        return commentDTO;
    }
}
