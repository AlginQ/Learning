package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.Category;
import com.fafu.learning.mapper.CategoryMapper;
import com.fafu.learning.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}