package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.Teacher;
import com.fafu.learning.mapper.TeacherMapper;
import com.fafu.learning.service.TeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 讲师服务实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public List<Teacher> getAllTeachers() {
        return list();
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return getById(id);
    }
    
    @Override
    public Teacher getByUserId(Long userId) {
        return getOne(new QueryWrapper<Teacher>().eq("user_id", userId));
    }
}