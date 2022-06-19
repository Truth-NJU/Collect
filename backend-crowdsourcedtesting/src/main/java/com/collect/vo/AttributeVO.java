package com.collect.vo;

import com.collect.annotation.UserId;
import com.collect.dto.AttributeDTO;
import com.collect.dto.DTO;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.PreferTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttributeVO implements VO {

    Integer id;

    @UserId
    Integer userId;

    List<Integer> device;

    List<Integer> preferTask;


    @Override
    public AttributeDTO toDTO() {
        return new AttributeDTO(null, userId, device, preferTask);
    }

    @Override
    public String toString() {
        return "AttributeVO{" +
                "id=" + id +
                ", userId=" + userId +
                ", device=" + device +
                ", preferTask=" + preferTask +
                '}';
    }
}
