package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.entity.Course;
import com.fafu.learning.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 教师课程管理控制器
 */
@RestController
@RequestMapping("/api/teacher/courses")
@CrossOrigin
public class TeacherCourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 获取教师的课程列表
     */
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<List<Course>> getTeacherCourses(HttpServletRequest request) {
        try {
            Long teacherId = getTeacherIdFromRequest(request);
            if (teacherId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
            return ApiResult.success(courses);
        } catch (Exception e) {
            return ApiResult.fail("获取课程列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建课程
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<Course> createCourse(@RequestBody Course course, HttpServletRequest request) {
        try {
            Long teacherId = getTeacherIdFromRequest(request);
            if (teacherId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            course.setTeacherId(teacherId);
            Course createdCourse = courseService.createCourse(course);
            return ApiResult.success("课程创建成功", createdCourse);
        } catch (Exception e) {
            return ApiResult.fail("课程创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<Course> updateCourse(@PathVariable Long id, @RequestBody Course course, HttpServletRequest request) {
        try {
            Long teacherId = getTeacherIdFromRequest(request);
            if (teacherId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            // 验证课程是否属于当前教师
            Course existingCourse = courseService.getById(id);
            if (existingCourse == null) {
                return ApiResult.notFound("课程不存在");
            }
            if (!existingCourse.getTeacherId().equals(teacherId)) {
                return ApiResult.forbidden("无权操作此课程");
            }
            
            course.setId(id);
            course.setTeacherId(teacherId); // 确保教师ID不变
            Course updatedCourse = courseService.updateCourse(course);
            return ApiResult.success("课程更新成功", updatedCourse);
        } catch (Exception e) {
            return ApiResult.fail("课程更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<String> deleteCourse(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long teacherId = getTeacherIdFromRequest(request);
            if (teacherId == null) {
                return ApiResult.unauthorized("未授权");
            }
            
            // 验证课程是否属于当前教师
            Course existingCourse = courseService.getById(id);
            if (existingCourse == null) {
                return ApiResult.notFound("课程不存在");
            }
            if (!existingCourse.getTeacherId().equals(teacherId)) {
                return ApiResult.forbidden("无权操作此课程");
            }
            
            courseService.deleteCourse(id);
            return ApiResult.success("课程删除成功");
        } catch (Exception e) {
            return ApiResult.fail("课程删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 从请求中获取教师ID
     */
    private Long getTeacherIdFromRequest(HttpServletRequest request) {
        // 实际项目中应从JWT token中解析用户ID，然后查询教师表获取教师ID
        // 这里为了演示，返回固定值
        return 1L;
    }
}