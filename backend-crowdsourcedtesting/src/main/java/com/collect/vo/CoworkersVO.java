package com.collect.vo;

import com.collect.dto.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoworkersVO implements VO{

    List<UserVO> coworkers;

    @Override
    public DTO toDTO() {
        return null;
    }
}
