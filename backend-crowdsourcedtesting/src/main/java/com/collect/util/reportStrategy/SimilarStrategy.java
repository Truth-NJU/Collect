package com.collect.util.reportStrategy;

import com.collect.dto.ReportDTO;
import com.collect.mapper.ReportImageMapper;
import com.collect.mapper.ReportMapper;
import com.collect.po.Report;
import com.collect.po.ReportImage;
import com.collect.util.HanlpUtil;
import com.collect.util.PhotoSimilarUtil;
import com.collect.vo.ReportVO;
import com.collect.vo.VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SimilarStrategy extends ReportStrategy {

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    ReportImageMapper reportImageMapper;

    @Override
    public List<VO> choose(int reportId,List<ReportDTO> reportDTOs) {
        //  判断报告文本相似度 and 报告图像相似度
        TreeMap<Double, List<ReportDTO>> similarityMap = new TreeMap<>();
        Report curReport = reportMapper.selectById(reportId);

        // 当前报告下的所有图片
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("reportId", curReport.getId());
        List<ReportImage> curReportImages = reportImageMapper.selectByMap(imageMap);
        List<String> curReportImagesPath = new ArrayList<>();
        for (ReportImage image : curReportImages) {
            curReportImagesPath.add(image.getImage());
        }
        for (ReportDTO reportDTO : reportDTOs) {
            // 报告文本的相似度
            double nodeSimilarity = HanlpUtil.calculateSimilarity(curReport.getNote(), reportDTO.getNote());
            double stepSimilarity = HanlpUtil.calculateSimilarity(curReport.getSteps(), reportDTO.getSteps());

            // 报告图片的相似度
            // 得到报告图片的路径列表
            Map<String, Object> imageMap2 = new HashMap<>();
            imageMap.put("reportId", reportDTO.getId());
            List<ReportImage> reportImages = reportImageMapper.selectByMap(imageMap2);
            List<String> reportImagesPath = new ArrayList<>();
            for (ReportImage image : reportImages) {
                reportImagesPath.add(image.getImage());
            }
            double imageSimilarity = PhotoSimilarUtil.comparePhotos(curReportImagesPath, reportImagesPath);

            // 计算得到最终的相似度
            double similarity = (nodeSimilarity + stepSimilarity + imageSimilarity) / 3;
            if (similarity < 0.5) {
                System.out.println(nodeSimilarity+" "+stepSimilarity+" "+imageSimilarity);
                continue;
            }
            if (similarityMap.containsKey(similarity)) {
                similarityMap.get(similarity).add(reportDTO);
            } else {
                List<ReportDTO> tmpDTO = new ArrayList<>();
                tmpDTO.add(reportDTO);
                similarityMap.put(similarity, tmpDTO);
            }
        }
        List<VO> ans = new ArrayList<>();
        while (ans.size() < 9 && !similarityMap.isEmpty()) {
            List<ReportDTO> list = similarityMap.get(similarityMap.lastKey());
            for (int i = 0; i < list.size() && ans.size() < 9; i++) {
                ReportVO reportVO = list.get(i).toVO();
                reportVO.setSimilarity(similarityMap.lastKey());
                ans.add(reportVO);
            }
            similarityMap.remove(similarityMap.lastKey());
        }
        return ans;
    }

}
