package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.StudyProgress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface StudyProgressMapper extends BaseMapper<StudyProgress> {
    
    @Select("SELECT * FROM study_progress WHERE user_id = #{userId} AND lesson_id = #{lessonId}")
    StudyProgress findByUserIdAndLessonId(@Param("userId") Long userId, @Param("lessonId") Long lessonId);
}