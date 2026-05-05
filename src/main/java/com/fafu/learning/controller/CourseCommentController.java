package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.service.CourseCommentService;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.vo.CourseCommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CourseCommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseCommentController.class);
    
    @Autowired
    private CourseCommentService courseCommentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add/{courseId}")
    public ApiResult<?> addComment(@PathVariable Long courseId, 
                                   @RequestBody CommentRequest requestBody,
                                   HttpServletRequest request) {
        logger.info("收到添加评论请求 - courseId: {}", courseId);
        
        String token = request.getHeader("Authorization");
        logger.info("Authorization token: {}", token);
        
        if (token == null || token.isEmpty()) {
            return ApiResult.unauthorized("请先登录");
        }
        
        // 移除 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        logger.info("解析出的 userId: {}", userId);
        
        if (userId == null) {
            return ApiResult.unauthorized("请先登录");
        }
        
        String content = requestBody.getContent();
        logger.info("评论内容: {}", content);
        
        if (content == null || content.trim().isEmpty()) {
            return ApiResult.badRequest("评论内容不能为空");
        }
        
        try {
            boolean success = courseCommentService.addComment(userId, courseId, content.trim());
            if (success) {
                return ApiResult.success("评论成功");
            }
            return ApiResult.fail("评论失败");
        } catch (Exception e) {
            logger.error("添加评论异常", e);
            return ApiResult.fail(500, "服务器内部错误: " + e.getMessage());
        }
    }
    
    public static class CommentRequest {
        private String content;
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    @GetMapping("/course/{courseId}")
    public ApiResult<List<CourseCommentVO>> getCommentsByCourseId(@PathVariable Long courseId) {
        logger.info("获取课程评论 - courseId: {}", courseId);
        try {
            List<CourseCommentVO> comments = courseCommentService.getCommentsByCourseId(courseId);
            return ApiResult.success(comments);
        } catch (Exception e) {
            logger.error("获取评论异常", e);
            return ApiResult.fail(500, "服务器内部错误: " + e.getMessage());
        }
    }
    
    @GetMapping("/count/{courseId}")
    public ApiResult<Integer> getCommentCount(@PathVariable Long courseId) {
        logger.info("获取评论数量 - courseId: {}", courseId);
        try {
            int count = courseCommentService.getCommentCount(courseId);
            return ApiResult.success(count);
        } catch (Exception e) {
            logger.error("获取评论数量异常", e);
            return ApiResult.fail(500, "服务器内部错误: " + e.getMessage());
        }
    }
    
    @PostMapping("/like/{commentId}")
    public ApiResult<?> likeComment(@PathVariable Long commentId) {
        logger.info("点赞评论 - commentId: {}", commentId);
        try {
            boolean success = courseCommentService.likeComment(commentId);
            if (success) {
                return ApiResult.success("点赞成功");
            }
            return ApiResult.fail("点赞失败");
        } catch (Exception e) {
            logger.error("点赞异常", e);
            return ApiResult.fail(500, "服务器内部错误: " + e.getMessage());
        }
    }
}