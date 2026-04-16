package com.fafu.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fafu.learning.common.ApiResult;
import com.fafu.learning.common.PageResult;
import com.fafu.learning.entity.Course;
import com.fafu.learning.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            @RequestParam(defaultValue = "8") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        try {
            System.out.println("Page: " + page + ", Size: " + size + ", Keyword: " + keyword);
            
            // 获取所有课程
            List<Course> allCourses = courseService.list();
            
            // 根据keyword过滤课程
            List<Course> filteredCourses = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                for (Course course : allCourses) {
                    if (course.getTitle() != null && course.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                        filteredCourses.add(course);
                    }
                }
            } else {
                filteredCourses = allCourses;
            }
            
            int total = filteredCourses.size();
            
            // 计算分页
            int start = (int) ((page - 1) * size);
            int end = Math.min(start + size.intValue(), total);
            List<Course> pageCourses = new ArrayList<>();
            if (start < total) {
                pageCourses = filteredCourses.subList(start, end);
            }
            
            System.out.println("Total: " + total + ", Records size: " + pageCourses.size());
            
            PageResult<Course> pageResult = new PageResult<>(
                pageCourses,
                (long) total,
                page,
                size
            );
            
            return ApiResult.success(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 添加课程
     */
    @PostMapping
    public ApiResult<Course> addCourse(@RequestBody Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return ApiResult.success("课程添加成功", createdCourse);
        } catch (Exception e) {
            return ApiResult.fail("添加课程失败: " + e.getMessage());
        }
    }

    /**
     * 编辑课程
     */
    @PutMapping("/{id}")
    public ApiResult<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            course.setId(id);
            Course updatedCourse = courseService.updateCourse(course);
            return ApiResult.success("课程更新成功", updatedCourse);
        } catch (Exception e) {
            return ApiResult.fail("更新课程失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ApiResult.success("课程删除成功", null);
        } catch (Exception e) {
            return ApiResult.fail("删除课程失败: " + e.getMessage());
        }
    }

    /**
     * 切换课程状态
     */
    @PutMapping("/{id}/status")
    public ApiResult<Course> toggleCourseStatus(@PathVariable Long id) {
        try {
            Course course = courseService.getById(id);
            if (course == null) {
                return ApiResult.notFound("课程不存在");
            }
            course.setStatus(course.getStatus() == 1 ? 0 : 1);
            courseService.updateCourse(course);
            return ApiResult.success("课程状态更新成功", course);
        } catch (Exception e) {
            return ApiResult.fail("更新课程状态失败: " + e.getMessage());
        }
    }

    /**
     * 切换课程推荐状态
     */
    @PutMapping("/{id}/recommend")
    public ApiResult<Course> toggleCourseRecommend(@PathVariable Long id) {
        try {
            Course course = courseService.getById(id);
            if (course == null) {
                return ApiResult.notFound("课程不存在");
            }
            course.setRecommend(course.getRecommend() == 1 ? 0 : 1);
            courseService.updateCourse(course);
            return ApiResult.success("课程推荐状态更新成功", course);
        } catch (Exception e) {
            return ApiResult.fail("更新课程推荐状态失败: " + e.getMessage());
        }
    }
}