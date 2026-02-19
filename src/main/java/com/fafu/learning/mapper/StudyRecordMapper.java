package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.StudyRecord;
import com.fafu.learning.vo.StudyRecordVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录Mapper接口
 */
public interface StudyRecordMapper extends BaseMapper<StudyRecord> {
    
    /**
     * 获取用户学习记录
     */
    @Select("SELECT sr.*, c.title as course_title, l.title as lesson_title, c.category_name as category, c.difficulty " +
            "FROM study_record sr " +
            "LEFT JOIN course c ON sr.course_id = c.id " +
            "LEFT JOIN lesson l ON sr.lesson_id = l.id " +
            "WHERE sr.user_id = #{userId} " +
            "AND sr.study_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY sr.study_time DESC")
    List<StudyRecordVO> getUserStudyRecords(@Param("userId") Long userId, 
                                          @Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取最近学习记录
     */
    @Select("SELECT sr.*, c.title as course_title, l.title as lesson_title, c.category_name as category, c.difficulty " +
            "FROM study_record sr " +
            "LEFT JOIN course c ON sr.course_id = c.id " +
            "LEFT JOIN lesson l ON sr.lesson_id = l.id " +
            "WHERE sr.user_id = #{userId} " +
            "ORDER BY sr.study_time DESC " +
            "LIMIT #{limit}")
    List<StudyRecordVO> getRecentStudyRecords(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 获取总学习时长（分钟）
     */
    @Select("SELECT SUM(duration) FROM study_record WHERE user_id = #{userId}")
    Double getTotalStudyTime(@Param("userId") Long userId);
    
    /**
     * 获取指定时间段内的学习时长
     */
    @Select("SELECT SUM(duration) FROM study_record " +
            "WHERE user_id = #{userId} " +
            "AND study_time BETWEEN #{startTime} AND #{endTime}")
    Double getStudyTimeByPeriod(@Param("userId") Long userId, 
                               @Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取完成课时数
     */
    @Select("SELECT COUNT(*) FROM study_record WHERE user_id = #{userId} AND progress = 100")
    Integer getCompletedLessonCount(@Param("userId") Long userId);
    
    /**
     * 获取完成课程数
     */
    @Select("SELECT COUNT(DISTINCT course_id) FROM study_record WHERE user_id = #{userId} AND progress = 100")
    Integer getCompletedCourseCount(@Param("userId") Long userId);
    
    /**
     * 获取总学习课程数
     */
    @Select("SELECT COUNT(DISTINCT course_id) FROM study_record WHERE user_id = #{userId}")
    Integer getTotalCourseCount(@Param("userId") Long userId);
    
    /**
     * 获取连续学习天数
     */
    @Select("SELECT COUNT(DISTINCT DATE(study_time)) as streak_days " +
            "FROM study_record " +
            "WHERE user_id = #{userId} " +
            "AND study_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)")
    Integer getCurrentStreak(@Param("userId") Long userId);
}