package com.collect.dto;

import com.collect.po.Report;
import com.collect.po.enums.WorkStatus;
import com.collect.vo.ReportVO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportDTO implements DTO {

    Integer id;
    Integer userId;
    Integer taskId;
    List<MultipartFile> images;
    List<String> paths;
    List<String> toKeep;
    String note;
    String steps;

    WorkStatus workStatus;

    String name;

    String email;

    String device;

    Double star;

    Integer starNum;

    public ReportDTO(Integer userId, Integer taskId, List<MultipartFile> images, String note, String steps, String device) {
        this.userId = userId;
        this.taskId = taskId;
        this.images = images;
        this.note = note;
        this.steps = steps;
        this.device = device;
    }

    public ReportDTO(Integer id, Integer userId, Integer taskId, List<MultipartFile> images, List<String> toKeep, String note, String steps, String device) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.images = images;
        this.note = note;
        this.steps = steps;
        this.device = device;
        this.toKeep = toKeep;
    }

    public ReportDTO(Integer id, Integer userId, Integer taskId, String note, String steps, String device) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.note = note;
        this.steps = steps;
        this.device = device;
    }


    @Override
    public Report toPO() {
        return new Report(id, userId, taskId, note, steps, device, star, starNum);
    }

    @Override
    public ReportVO toVO() {
        return new ReportVO(id, userId, taskId, images,paths, toKeep,note, steps, device, name,email, workStatus,star,starNum,null,null);
    }
}