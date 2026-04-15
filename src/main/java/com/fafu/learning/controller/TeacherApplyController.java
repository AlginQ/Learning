package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.entity.TeacherApply;
import com.fafu.learning.service.TeacherApplyService;
import com.fafu.learning.dto.TeacherApplyDTO;
import com.fafu.learning.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 教师申请控制器
 */
@RestController
@RequestMapping("/api/teacher-apply")
@CrossOrigin
public class TeacherApplyController {
    
    @Autowired
    private TeacherApplyService teacherApplyService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 提交教师申请
     */
    @PostMapping
    public ApiResult<String> submitApply(@RequestBody TeacherApplyDTO applyDTO, HttpServletRequest request) {
        try {
            // 从请求中获取用户ID（实际项目中应从JWT token中解析）
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            teacherApplyService.submitApply(applyDTO, userId);
            return ApiResult.success("申请提交成功，等待管理员审核");
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("申请提交失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的申请状态
     */
    @GetMapping("/status")
    public ApiResult<TeacherApply> getApplyStatus(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            TeacherApply apply = teacherApplyService.getApplyByUserId(userId);
            return ApiResult.success(apply);
        } catch (Exception e) {
            return ApiResult.fail("获取申请状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有待审核的申请（管理员功能）
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherApply>> getPendingApplies() {
        try {
            List<TeacherApply> applies = teacherApplyService.getPendingApplies();
            return ApiResult.success(applies);
        } catch (Exception e) {
            return ApiResult.fail("获取待审核申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核教师申请（管理员功能）
     */
    @PostMapping("/review/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<String> reviewApply(
            @PathVariable Long id,
            @RequestBody ReviewRequest reviewRequest,
            HttpServletRequest request) {
        try {
            Long reviewerId = getUserIdFromRequest(request);
            if (reviewerId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            teacherApplyService.reviewApply(id, reviewRequest.getStatus(), reviewRequest.getRemark(), reviewerId);
            return ApiResult.success("审核成功");
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("审核失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核请求DTO
     */
    private static class ReviewRequest {
        private Integer status;
        private String remark;
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public String getRemark() {
            return remark;
        }
        
        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 从请求属性中获取用户ID（由JwtAuthenticationFilter设置）
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return (Long) userIdObj;
        }
        
        // 如果请求属性中没有，尝试从JWT token中解析
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // 使用JWT工具类解析token获取用户ID
                return jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}