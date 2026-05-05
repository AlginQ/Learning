package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.CourseComment;
import com.fafu.learning.mapper.CourseCommentMapper;
import com.fafu.learning.service.CourseCommentService;
import com.fafu.learning.vo.CourseCommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseCommentServiceImpl extends ServiceImpl<CourseCommentMapper, CourseComment> implements CourseCommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseCommentServiceImpl.class);
    
    @Override
    public boolean addComment(Long userId, Long courseId, String content) {
        logger.info("添加评论 - userId: {}, courseId: {}, content: {}", userId, courseId, content);
        
        CourseComment comment = new CourseComment();
        comment.setUserId(userId);
        comment.setCourseId(courseId);
        comment.setContent(content);
        comment.setRating(0);
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        try {
            boolean result = save(comment);
            logger.info("评论保存结果: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("保存评论失败", e);
            throw e;
        }
    }
    
    @Override
    public List<CourseCommentVO> getCommentsByCourseId(Long courseId) {
        return baseMapper.getCommentsByCourseId(courseId);
    }
    
    @Override
    public int getCommentCount(Long courseId) {
        return baseMapper.countByCourseId(courseId);
    }
    
    @Override
    public boolean likeComment(Long commentId) {
        UpdateWrapper<CourseComment> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", commentId);
        wrapper.setSql("like_count = like_count + 1");
        return update(wrapper);
    }
}