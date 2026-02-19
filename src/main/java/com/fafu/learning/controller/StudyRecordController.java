package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.service.StudyRecordService;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.vo.StudyRecordVO;
import com.fafu.learning.vo.StudyStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录控制器
 */
@RestController
@RequestMapping("/api/study")
@CrossOrigin
public class StudyRecordController {
    
    @Autowired
    private StudyRecordService studyRecordService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 添加学习记录
     */
    @PostMapping("/record")
    public ApiResult<Void> addStudyRecord(
            HttpServletRequest request,
            @RequestParam Long courseId,
            @RequestParam Long lessonId,
            @RequestParam Integer duration,
            @RequestParam Integer progress) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    studyRecordService.addStudyRecord(userId, courseId, lessonId, duration, progress);
                    return ApiResult.success("学习记录添加成功", null);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 获取用户学习记录
     */
    @GetMapping("/records")
    public ApiResult<List<StudyRecordVO>> getUserStudyRecords(
            HttpServletRequest request,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    LocalDateTime startTime = startDate != null ? 
                        LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
                    LocalDateTime endTime = endDate != null ? 
                        LocalDateTime.parse(endDate) : LocalDateTime.now();
                    
                    List<StudyRecordVO> records = studyRecordService.getUserStudyRecords(userId, startTime, endTime);
                    return ApiResult.success(records);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 获取用户学习统计
     */
    @GetMapping("/statistics")
    public ApiResult<StudyStatisticsVO> getUserStudyStatistics(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    StudyStatisticsVO statistics = studyRecordService.getUserStudyStatistics(userId);
                    return ApiResult.success(statistics);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 获取最近学习记录
     */
    @GetMapping("/recent")
    public ApiResult<List<StudyRecordVO>> getRecentStudyRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    List<StudyRecordVO> records = studyRecordService.getRecentStudyRecords(userId, limit);
                    return ApiResult.success(records);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
}