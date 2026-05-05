package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.CourseComment;
import com.fafu.learning.vo.CourseCommentVO;

import java.util.List;

public interface CourseCommentService extends IService<CourseComment> {
    
    boolean addComment(Long userId, Long courseId, String content);
    
    List<CourseCommentVO> getCommentsByCourseId(Long courseId);
    
    int getCommentCount(Long courseId);
    
    boolean likeComment(Long commentId);
}