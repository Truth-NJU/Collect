package com.collect.dto;

import com.collect.po.PO;
import com.collect.vo.VO;

public interface DTO {
    PO toPO();

    VO toVO();
}
