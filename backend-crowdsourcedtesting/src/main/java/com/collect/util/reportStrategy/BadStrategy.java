package com.collect.util.reportStrategy;

import com.collect.dto.ReportDTO;
import com.collect.vo.VO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class BadStrategy extends ReportStrategy {

    @Override
    public List<VO> choose(int reportId,List<ReportDTO> reportDTOs) {
        reportDTOs.sort(Comparator.comparing(ReportDTO::getStar));
        List<VO> ans=new ArrayList<>();
        for (int i=0;i<Math.min(reportDTOs.size(),9);i++){
            ans.add(reportDTOs.get(i).toVO());
        }
        return ans;
    }

}
