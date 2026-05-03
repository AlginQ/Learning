package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.service.StudyRecordService;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.vo.StudyRecordVO;
import com.fafu.learning.vo.StudyStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
            @RequestBody(required = false) StudyRecordRequest requestBody) {
        try {
            System.out.println("接收到学习记录请求");
            System.out.println("请求体: " + requestBody);
            
            // 检查请求体是否为空
            if (requestBody == null) {
                System.out.println("请求体为 null");
                return ApiResult.badRequest("请求体不能为空");
            }
            
            // 验证参数
            System.out.println("courseId: " + requestBody.getCourseId());
            System.out.println("lessonId: " + requestBody.getLessonId());
            System.out.println("duration: " + requestBody.getDuration());
            System.out.println("progress: " + requestBody.getProgress());
            
            if (requestBody.getCourseId() == null) {
                System.out.println("courseId 为 null");
                return ApiResult.badRequest("课程ID不能为空");
            }
            if (requestBody.getLessonId() == null) {
                System.out.println("lessonId 为 null");
                return ApiResult.badRequest("课时ID不能为空");
            }
            if (requestBody.getDuration() == null) {
                System.out.println("duration 为 null");
                return ApiResult.badRequest("学习时长不能为空");
            }
            if (requestBody.getProgress() == null) {
                System.out.println("progress 为 null");
                return ApiResult.badRequest("学习进度不能为空");
            }
            
            System.out.println("参数验证通过");
            
            String token = request.getHeader("Authorization");
            System.out.println("接收到的token: " + token);
            
            if (token == null) {
                System.out.println("token 为 null");
                return ApiResult.unauthorized("未授权: token 为 null");
            }
            
            if (!token.startsWith("Bearer ")) {
                System.out.println("token 格式不正确");
                return ApiResult.unauthorized("未授权: token 格式不正确");
            }
            
            token = token.substring(7);
            System.out.println("提取后的token: " + token);
            
            Long userId = null;
            try {
                userId = jwtUtil.getUserIdFromToken(token);
                System.out.println("从token中提取的userId: " + userId);
            } catch (Exception e) {
                System.out.println("解析token异常: " + e.getMessage());
                e.printStackTrace();
                return ApiResult.unauthorized("未授权: " + e.getMessage());
            }
            
            if (userId == null) {
                System.out.println("userId 为 null");
                return ApiResult.unauthorized("未授权: userId 为 null");
            }
            
            System.out.println("准备添加学习记录");
            System.out.println("userId: " + userId);
            System.out.println("courseId: " + requestBody.getCourseId());
            System.out.println("lessonId: " + requestBody.getLessonId());
            System.out.println("duration: " + requestBody.getDuration());
            System.out.println("progress: " + requestBody.getProgress());
            
            try {
                studyRecordService.addStudyRecord(
                        userId, 
                        requestBody.getCourseId(), 
                        requestBody.getLessonId(), 
                        requestBody.getDuration(), 
                        requestBody.getProgress()
                );
                System.out.println("学习记录添加成功");
                return ApiResult.success("学习记录添加成功", null);
            } catch (Exception e) {
                System.out.println("添加学习记录异常: " + e.getMessage());
                e.printStackTrace();
                return ApiResult.badRequest("添加学习记录失败: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("添加学习记录异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.badRequest("添加学习记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 学习记录请求DTO
     */
    public static class StudyRecordRequest {
        private Long courseId;
        private Long lessonId;
        private Integer duration;
        private Integer progress;
        
        // 无参构造函数
        public StudyRecordRequest() {
        }
        
        // Getters and setters
        public Long getCourseId() {
            return courseId;
        }
        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }
        public Long getLessonId() {
            return lessonId;
        }
        public void setLessonId(Long lessonId) {
            this.lessonId = lessonId;
        }
        public Integer getDuration() {
            return duration;
        }
        public void setDuration(Integer duration) {
            this.duration = duration;
        }
        public Integer getProgress() {
            return progress;
        }
        public void setProgress(Integer progress) {
            this.progress = progress;
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
                    // 默认查询所有历史记录，不再限制30天
                    LocalDateTime startTime = startDate != null ? 
                        LocalDateTime.parse(startDate) : LocalDateTime.of(2000, 1, 1, 0, 0);
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