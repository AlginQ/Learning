package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.entity.Teacher;
import com.fafu.learning.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 讲师控制器
 */
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 获取所有讲师
     */
    @GetMapping
    public ApiResult<List<Teacher>> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherService.getAllTeachers();
            return ApiResult.success(teachers);
        } catch (Exception e) {
            return ApiResult.fail("获取讲师列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取讲师
     */
    @GetMapping("/{id}")
    public ApiResult<Teacher> getTeacherById(Long id) {
        try {
            Teacher teacher = teacherService.getTeacherById(id);
            if (teacher == null) {
                return ApiResult.notFound("讲师不存在");
            }
            return ApiResult.success(teacher);
        } catch (Exception e) {
            return ApiResult.fail("获取讲师详情失败: " + e.getMessage());
        }
    }
}