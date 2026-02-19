package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.StudyRecord;
import com.fafu.learning.vo.StudyRecordVO;
import com.fafu.learning.vo.StudyStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录服务接口
 */
public interface StudyRecordService extends IService<StudyRecord> {
    
    /**
     * 添加学习记录
     */
    void addStudyRecord(Long userId, Long courseId, Long lessonId, Integer duration, Integer progress);
    
    /**
     * 获取用户学习记录
     */
    List<StudyRecordVO> getUserStudyRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取用户学习统计
     */
    StudyStatisticsVO getUserStudyStatistics(Long userId);
    
    /**
     * 获取最近学习记录
     */
    List<StudyRecordVO> getRecentStudyRecords(Long userId, int limit);
    
    /**
     * 获取用户完成的课程数量
     */
    Integer getCompletedCourseCount(Long userId);
    
    /**
     * 获取用户总学习时长（小时）
     */
    Double getTotalStudyTime(Long userId);
}