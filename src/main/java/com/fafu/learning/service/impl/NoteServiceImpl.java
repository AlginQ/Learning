package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.Note;
import com.fafu.learning.mapper.NoteMapper;
import com.fafu.learning.service.NoteService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 笔记Service实现类
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {
    
    @Override
    public List<Note> getNotesByUserId(Long userId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                .orderByDesc(Note::getUpdateTime);
        return baseMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<Note> getNotesByUserIdAndCourseId(Long userId, Long courseId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                .eq(Note::getCourseId, courseId)
                .orderByDesc(Note::getUpdateTime);
        return baseMapper.selectList(queryWrapper);
    }
    
    @Override
    public Note getNoteByUserIdAndCourseIdAndLessonId(Long userId, Long courseId, Long lessonId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                .eq(Note::getCourseId, courseId)
                .eq(Note::getLessonId, lessonId);
        return baseMapper.selectOne(queryWrapper);
    }
    
    @Override
    public List<Note> searchNotesByUserId(Long userId, String keyword) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                .and(wrapper -> wrapper.like(Note::getTitle, keyword).or().like(Note::getContent, keyword))
                .orderByDesc(Note::getUpdateTime);
        return baseMapper.selectList(queryWrapper);
    }
}