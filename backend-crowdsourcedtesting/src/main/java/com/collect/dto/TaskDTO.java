package com.collect.dto;

import com.collect.po.Task;
import com.collect.po.enums.DeviceType;
import com.collect.vo.TaskVO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskDTO implements DTO {
    Integer id;

    Integer userId;

    String name;

    Integer number;

    Integer remain;

    Integer tag;

    Long date;

    String aurl;

    String purl;

    MultipartFile apk;

    MultipartFile pdf;

    Boolean deleteApk;

    Boolean deletePdf;

    String introduction;

    Integer level;

    DeviceType device;


    public Task toPO() {
        return new Task(id, userId, name, number, remain, tag, date, aurl, purl, introduction,level,device);
    }

    @Override
    public TaskVO toVO() {
        TaskVO taskVO = new TaskVO();
        taskVO.setId(id);
        taskVO.setUserId(userId);
        taskVO.setName(name);
        taskVO.setNumber(number);
        taskVO.setRemain(remain);
        taskVO.setTag(tag);
        taskVO.setDate(date);
        taskVO.setAurl(aurl);
        taskVO.setPurl(purl);
        taskVO.setIntroduction(introduction);
        taskVO.setDevice(device);
        return taskVO;
    }

}
