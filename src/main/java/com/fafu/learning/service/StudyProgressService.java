package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.StudyProgress;

public interface StudyProgressService extends IService<StudyProgress> {
    
    /**
     * 获取用户的课时学习进度
     */
    StudyProgress getProgress(Long userId, Long lessonId);
    
    /**
     * 保存或更新学习进度
     */
    boolean saveOrUpdateProgress(Long userId, Long lessonId, Integer progress, Integer watchDuration, Boolean isCompleted);
}