package com.collect.util.reportStrategy;

import com.collect.dto.ReportDTO;
import com.collect.vo.VO;

import java.util.List;

public abstract class ReportStrategy {

    public abstract List<VO> choose(int reportId, List<ReportDTO>reportDTOs);

}
