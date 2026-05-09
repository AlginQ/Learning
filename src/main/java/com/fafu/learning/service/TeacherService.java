package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.Teacher;

import java.util.List;

/**
 * 讲师服务接口
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 获取所有讲师
     */
    List<Teacher> getAllTeachers();

    /**
     * 根据ID获取讲师
     */
    Teacher getTeacherById(Long id);
    
    /**
     * 根据用户ID获取教师信息
     */
    Teacher getByUserId(Long userId);
}