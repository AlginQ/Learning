package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.StudyRecord;
import com.fafu.learning.mapper.StudyRecordMapper;
import com.fafu.learning.service.StudyRecordService;
import com.fafu.learning.vo.StudyRecordVO;
import com.fafu.learning.vo.StudyStatisticsVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录服务实现类
 */
@Service
public class StudyRecordServiceImpl extends ServiceImpl<StudyRecordMapper, StudyRecord> implements StudyRecordService {
    
    @Override
    public void addStudyRecord(Long userId, Long courseId, Long lessonId, Integer duration, Integer progress) {
        StudyRecord record = new StudyRecord();
        record.setUserId(userId);
        record.setCourseId(courseId);
        record.setLessonId(lessonId);
        record.setDuration(duration);
        record.setProgress(progress);
        record.setStudyTime(LocalDateTime.now());
        save(record);
    }
    
    @Override
    public List<StudyRecordVO> getUserStudyRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.getUserStudyRecords(userId, startTime, endTime);
    }
    
    @Override
    public StudyStatisticsVO getUserStudyStatistics(Long userId) {
        StudyStatisticsVO stats = new StudyStatisticsVO();
        
        // 总学习时长
        Double totalMinutes = baseMapper.getTotalStudyTime(userId);
        stats.setTotalStudyTime(totalMinutes != null ? totalMinutes / 60 : 0.0);
        
        // 完成课时数
        stats.setCompletedLessons(baseMapper.getCompletedLessonCount(userId));
        
        // 完成课程数
        stats.setCompletedCourses(getCompletedCourseCount(userId));
        
        // 连续学习天数
        stats.setCurrentStreak(baseMapper.getCurrentStreak(userId));
        
        // 总学习课程数
        stats.setTotalCourses(baseMapper.getTotalCourseCount(userId));
        
        // 今日学习时长
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        Double todayMinutes = baseMapper.getStudyTimeByPeriod(userId, todayStart, todayEnd);
        stats.setTodayStudyTime(todayMinutes != null ? todayMinutes / 60 : 0.0);
        
        // 本周学习时长
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        Double weekMinutes = baseMapper.getStudyTimeByPeriod(userId, weekStart, LocalDateTime.now());
        stats.setWeekStudyTime(weekMinutes != null ? weekMinutes / 60 : 0.0);
        
        // 本月学习时长
        LocalDateTime monthStart = LocalDate.now().minusDays(30).atStartOfDay();
        Double monthMinutes = baseMapper.getStudyTimeByPeriod(userId, monthStart, LocalDateTime.now());
        stats.setMonthStudyTime(monthMinutes != null ? monthMinutes / 60 : 0.0);
        
        return stats;
    }
    
    @Override
    public List<StudyRecordVO> getRecentStudyRecords(Long userId, int limit) {
        return baseMapper.getRecentStudyRecords(userId, limit);
    }
    
    @Override
    public Integer getCompletedCourseCount(Long userId) {
        return baseMapper.getCompletedCourseCount(userId);
    }
    
    @Override
    public Double getTotalStudyTime(Long userId) {
        Double minutes = baseMapper.getTotalStudyTime(userId);
        return minutes != null ? minutes / 60 : 0.0;
    }
}