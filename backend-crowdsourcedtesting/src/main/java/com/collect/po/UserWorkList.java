package com.collect.po;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserWorkList {
    Integer userId;

    List<Integer> taskId;
}
