package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.CourseComment;
import com.fafu.learning.vo.CourseCommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CourseCommentMapper extends BaseMapper<CourseComment> {
    
    @Select("SELECT cr.id, cr.course_id as courseId, cr.user_id as userId, u.username, " +
            "cr.rating, cr.content, cr.like_count as likes, cr.create_time as createTime " +
            "FROM course_review cr " +
            "LEFT JOIN user u ON cr.user_id = u.id " +
            "WHERE cr.course_id = #{courseId} AND cr.status = 1 " +
            "ORDER BY cr.create_time DESC")
    List<CourseCommentVO> getCommentsByCourseId(@Param("courseId") Long courseId);
    
    @Select("SELECT COUNT(*) FROM course_review WHERE course_id = #{courseId} AND status = 1")
    int countByCourseId(@Param("courseId") Long courseId);
}