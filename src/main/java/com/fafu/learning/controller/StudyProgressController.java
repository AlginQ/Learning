package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.entity.StudyProgress;
import com.fafu.learning.service.StudyProgressService;
import com.fafu.learning.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/progress")
public class StudyProgressController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudyProgressController.class);
    
    @Autowired
    private StudyProgressService studyProgressService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/{lessonId}")
    public ApiResult<StudyProgress> getProgress(@PathVariable Long lessonId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return ApiResult.unauthorized("请先登录");
        }
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ApiResult.unauthorized("请先登录");
        }
        
        StudyProgress progress = studyProgressService.getProgress(userId, lessonId);
        return ApiResult.success(progress);
    }
    
    @PostMapping("/{lessonId}")
    public ApiResult<?> saveProgress(@PathVariable Long lessonId, 
                                     @RequestBody ProgressRequest requestBody,
                                     HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return ApiResult.unauthorized("请先登录");
        }
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ApiResult.unauthorized("请先登录");
        }
        
        boolean success = studyProgressService.saveOrUpdateProgress(userId, lessonId, requestBody.getProgress(), requestBody.getWatchDuration(), requestBody.getIsCompleted());
        
        if (success) {
            return ApiResult.success("保存成功");
        }
        return ApiResult.fail("保存失败");
    }
    
    public static class ProgressRequest {
        private Integer progress;
        private Integer watchDuration;
        private Boolean isCompleted;
        
        public Integer getProgress() {
            return progress;
        }
        
        public void setProgress(Integer progress) {
            this.progress = progress;
        }
        
        public Integer getWatchDuration() {
            return watchDuration;
        }
        
        public void setWatchDuration(Integer watchDuration) {
            this.watchDuration = watchDuration;
        }
        
        public Boolean getIsCompleted() {
            return isCompleted;
        }
        
        public void setIsCompleted(Boolean isCompleted) {
            this.isCompleted = isCompleted;
        }
    }
}