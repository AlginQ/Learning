package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.Note;
import java.util.List;

/**
 * 笔记Service接口
 */
public interface NoteService extends IService<Note> {
    
    /**
     * 根据用户ID获取笔记列表
     */
    List<Note> getNotesByUserId(Long userId);
    
    /**
     * 根据用户ID和课程ID获取笔记列表
     */
    List<Note> getNotesByUserIdAndCourseId(Long userId, Long courseId);
    
    /**
     * 根据用户ID和课程ID和课时ID获取笔记
     */
    Note getNoteByUserIdAndCourseIdAndLessonId(Long userId, Long courseId, Long lessonId);
    
    /**
     * 搜索笔记
     */
    List<Note> searchNotesByUserId(Long userId, String keyword);
}