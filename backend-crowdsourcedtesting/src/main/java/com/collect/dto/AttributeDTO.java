package com.collect.dto;

import com.collect.po.Attribute;
import com.collect.po.PO;
import com.collect.po.enums.DeviceType;
import com.collect.po.enums.PreferTask;
import com.collect.vo.AttributeVO;
import com.collect.vo.VO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttributeDTO implements DTO {

    Integer id;

    Integer userId;

    List<Integer> device;

    List<Integer> preferTask;

    @Override
    public Attribute toPO() {
        Attribute attribute = new Attribute();
        for (int i : device) {
            switch (i) {
                case 0:
                    attribute.setWindows(10);
                    break;
                case 1:
                    attribute.setLinux(10);
                    break;
                case 2:
                    attribute.setMac(10);
                    break;
                case 3:
                    attribute.setAndroid(10);
                    break;
                case 4:
                    attribute.setIos(10);
                    break;
                case 5:
                    attribute.setHarmonyos(10);
                    break;
            }
        }
        for (int i : preferTask) {
            switch (i) {
                case 1:
                    attribute.setPerformance(10);
                    break;
                case 0:
                    attribute.setFunctional(10);
                    break;
                case 2:
                    attribute.setBug(10);
                    break;
            }
        }
        attribute.setUserId(userId);
        return attribute;
    }

    @Override
    public AttributeVO toVO() {
        return new AttributeVO(id, userId, new ArrayList<>(), new ArrayList<>());
    }
}
