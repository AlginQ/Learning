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
    
    /**
     * 获取待审核课程列表
     */
    List<Course> getPendingCourses();
    
    /**
     * 审核课程
     * @param courseId 课程ID
     * @param auditStatus 审核状态 1-通过 2-拒绝
     */
    void auditCourse(Long courseId, Integer auditStatus);
    
    /**
     * 获取教师课程列表（包含审核状态）
     */
    List<Course> getTeacherCoursesWithAuditStatus(Long teacherId);
}