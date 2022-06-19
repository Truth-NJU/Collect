package com.collect.vo;


import com.collect.annotation.UserId;
import com.collect.dto.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentVO implements VO {
    @UserId
    Integer id;

    Integer reportId;

    Integer userId;

    Double mark;

    String comment;

    String name;

    String email;

    @Override
    public CommentDTO toDTO() {
        return new CommentDTO(id, reportId, userId, mark, comment, name, email);
    }
}
