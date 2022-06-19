package com.collect.vo;

import com.collect.dto.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClusterVO implements VO{

    List<List<Integer>> result;

    List<String> path;

    @Override
    public DTO toDTO() {
        return null;
    }
}
