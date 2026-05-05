package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.StudyProgress;
import com.fafu.learning.mapper.StudyProgressMapper;
import com.fafu.learning.service.StudyProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StudyProgressServiceImpl extends ServiceImpl<StudyProgressMapper, StudyProgress> implements StudyProgressService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudyProgressServiceImpl.class);
    
    @Override
    public StudyProgress getProgress(Long userId, Long lessonId) {
        logger.info("获取学习进度 - userId: {}, lessonId: {}", userId, lessonId);
        return baseMapper.findByUserIdAndLessonId(userId, lessonId);
    }
    
    @Override
    public boolean saveOrUpdateProgress(Long userId, Long lessonId, Integer progress, Integer watchDuration, Boolean isCompleted) {
        logger.info("保存学习进度 - userId: {}, lessonId: {}, progress: {}, watchDuration: {}, isCompleted: {}", 
                   userId, lessonId, progress, watchDuration, isCompleted);
        
        // 先查找是否存在记录
        StudyProgress existing = baseMapper.findByUserIdAndLessonId(userId, lessonId);
        
        if (existing != null) {
            // 更新现有记录
            UpdateWrapper<StudyProgress> wrapper = new UpdateWrapper<>();
            wrapper.eq("user_id", userId).eq("lesson_id", lessonId);
            wrapper.set("progress", progress);
            wrapper.set("watch_duration", watchDuration);
            wrapper.set("is_completed", isCompleted ? 1 : 0);
            wrapper.set("last_study_time", LocalDateTime.now());
            return update(wrapper);
        } else {
            // 创建新记录
            StudyProgress progressEntity = new StudyProgress();
            progressEntity.setUserId(userId);
            progressEntity.setLessonId(lessonId);
            progressEntity.setProgress(progress);
            progressEntity.setWatchDuration(watchDuration);
            progressEntity.setIsCompleted(isCompleted ? 1 : 0);
            progressEntity.setLastStudyTime(LocalDateTime.now());
            return save(progressEntity);
        }
    }
}