package com.collect.service;

import com.collect.dto.CommentDTO;
import com.collect.dto.ReportDTO;
import com.collect.util.reportStrategy.ReportStrategy;
import com.collect.vo.ReportVO;
import com.collect.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */

public interface ReportService {

    ResponseVO lookReports(Integer taskId);

    ResponseVO commitReport(ReportDTO reportDTO);

    @Deprecated
    ResponseVO updateReport(ReportDTO reportDTO);

    ResponseVO findReport(Integer userId, Integer taskId);

    ResponseVO findTargetReports(ReportStrategy reportStrategy, int reportId);

    ResponseVO giveMarkAndComment(CommentDTO commentDTO);

    ResponseVO getCommentsUnderReport(int reportId);

    ResponseVO giveAnnotation(ReportDTO reportDTO,Integer annotationUserId);

    ResponseVO showAnnotation(int taskId, int userId);

    ResponseVO findCoworkers(int taskId, int userId);

    ResponseVO reportsSimilarity(int taskId);

    ResponseVO findClusterResult(int taskId);
}
