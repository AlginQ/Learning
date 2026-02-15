package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.entity.Category;
import com.fafu.learning.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有分类
     */
    @GetMapping
    public ApiResult<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.list();
            return ApiResult.success(categories);
        } catch (Exception e) {
            return ApiResult.fail("获取分类失败: " + e.getMessage());
        }
    }
}