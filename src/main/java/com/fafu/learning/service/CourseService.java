package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.Course;

import java.util.List;
import java.util.Map;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 根据教师ID获取课程列表
     */
    List<Course> getCoursesByTeacherId(Long teacherId);
    
    /**
     * 创建课程
     */
    Course createCourse(Course course);
    
    /**
     * 更新课程
     */
    Course updateCourse(Course course);
    
    /**
     * 删除课程
     */
    void deleteCourse(Long id);
}