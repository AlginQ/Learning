package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.Course;
import com.fafu.learning.mapper.CourseMapper;
import com.fafu.learning.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}