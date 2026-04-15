package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.Course;
import com.fafu.learning.mapper.CourseMapper;
import com.fafu.learning.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务实现
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return list(new QueryWrapper<Course>().eq("teacher_id", teacherId).orderByDesc("create_time"));
    }
    
    @Override
    @Transactional
    public Course createCourse(Course course) {
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        course.setStudentCount(0);
        course.setRating(BigDecimal.ZERO);
        course.setStatus(1); // 默认为上架状态
        save(course);
        return course;
    }
    
    @Override
    @Transactional
    public Course updateCourse(Course course) {
        course.setUpdateTime(LocalDateTime.now());
        updateById(course);
        return course;
    }
    
    @Override
    @Transactional
    public void deleteCourse(Long id) {
        removeById(id);
    }
}