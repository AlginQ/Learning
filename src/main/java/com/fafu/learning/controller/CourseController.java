package com.fafu.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fafu.learning.common.ApiResult;
import com.fafu.learning.common.PageResult;
import com.fafu.learning.entity.Course;
import com.fafu.learning.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 获取课程列表
     */
    @GetMapping
    public ApiResult<PageResult<Course>> getCourseList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        try {
            Page<Course> pageParam = new Page<>(page, size);
            Page<Course> result = courseService.page(pageParam);
            
            PageResult<Course> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return ApiResult.success(pageResult);
        } catch (Exception e) {
            return ApiResult.fail("获取课程列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public ApiResult<Course> getCourseDetail(@PathVariable Long id) {
        try {
            Course course = courseService.getById(id);
            if (course == null) {
                return ApiResult.notFound("课程不存在");
            }
            return ApiResult.success(course);
        } catch (Exception e) {
            return ApiResult.fail("获取课程详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public ApiResult<PageResult<Course>> searchCourses(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        try {
            Page<Course> pageParam = new Page<>(page, size);
            Page<Course> result = courseService.page(pageParam);
            
            PageResult<Course> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return ApiResult.success(pageResult);
        } catch (Exception e) {
            return ApiResult.fail("搜索课程失败: " + e.getMessage());
        }
    }
}