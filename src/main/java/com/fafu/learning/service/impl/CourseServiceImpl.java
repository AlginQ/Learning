package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.Course;
import com.fafu.learning.entity.Chapter;
import com.fafu.learning.entity.Lesson;
import com.fafu.learning.mapper.CourseMapper;
import com.fafu.learning.mapper.LessonMapper;
import com.fafu.learning.mapper.ChapterMapper;
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
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private LessonMapper lessonMapper;
    
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
        course.setStatus(0); // 默认下架状态，等待审核
        course.setAuditStatus(0); // 0-待审核
        save(course);
        
        // 保存章节和课时数据
        List<Lesson> lessons = course.getLessons();
        System.out.println("=== 章节数据调试 ===");
        System.out.println("章节列表: " + lessons);
        System.out.println("章节列表是否为空: " + (lessons == null || lessons.isEmpty()));
        
        if (lessons != null && !lessons.isEmpty()) {
            int chapterNumber = 1;
            for (Lesson lesson : lessons) {
                try {
                    // 1. 先保存章节
                    System.out.println("=== 开始保存章节 ===");
                    System.out.println("章节标题: " + lesson.getTitle());
                    System.out.println("课程ID: " + course.getId());
                    
                    Chapter chapter = new Chapter();
                    chapter.setCourseId(course.getId());
                    chapter.setTitle(lesson.getTitle());
                    chapter.setChapterNumber(chapterNumber++);
                    chapter.setCreateTime(LocalDateTime.now());
                    chapter.setUpdateTime(LocalDateTime.now());
                    
                    int chapterResult = chapterMapper.insert(chapter);
                    System.out.println("章节插入结果: " + chapterResult);
                    System.out.println("章节ID: " + chapter.getId());
                    
                    if (chapterResult > 0) {
                        System.out.println("保存章节成功: " + chapter.getTitle());
                        
                        // 2. 再保存课时，关联到刚创建的章节
                        System.out.println("=== 开始保存课时 ===");
                        lesson.setCourseId(course.getId());
                        lesson.setChapterId(chapter.getId()); // 关联到刚创建的章节
                        lesson.setIsFree(1); // 默认免费
                        lesson.setCreateTime(LocalDateTime.now());
                        lesson.setUpdateTime(LocalDateTime.now());
                        
                        int lessonResult = lessonMapper.insert(lesson);
                        System.out.println("课时插入结果: " + lessonResult);
                        System.out.println("课时ID(数据库自增): " + lesson.getId());
                        System.out.println("课时序号(lessonNumber): " + lesson.getLessonNumber());
                        
                        if (lessonResult > 0) {
                            System.out.println("保存课时成功: " + lesson.getTitle() + ", ID: " + lesson.getId() + ", 视频URL: " + lesson.getVideoUrl() + ", 时长: " + lesson.getDuration());
                        } else {
                            System.err.println("保存课时失败");
                        }
                    } else {
                        System.err.println("保存章节失败");
                    }
                } catch (Exception e) {
                    System.err.println("保存章节或课时时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
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
        System.out.println("=== 开始删除课程 ===");
        System.out.println("课程ID: " + id);
        
        try {
            // 1. 删除课时（lesson表）
            int lessonDeleted = lessonMapper.delete(new QueryWrapper<Lesson>().eq("course_id", id));
            System.out.println("删除课时数量: " + lessonDeleted);
            
            // 2. 删除章节（chapter表）
            int chapterDeleted = chapterMapper.delete(new QueryWrapper<Chapter>().eq("course_id", id));
            System.out.println("删除章节数量: " + chapterDeleted);
            
            // 3. 删除课程（course表）
            boolean courseDeleted = removeById(id);
            System.out.println("删除课程结果: " + courseDeleted);
            
            System.out.println("=== 删除课程完成 ===");
        } catch (Exception e) {
            System.err.println("删除课程时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public List<Course> getPendingCourses() {
        return list(new QueryWrapper<Course>().eq("audit_status", 0).orderByDesc("create_time"));
    }
    
    @Override
    @Transactional
    public void auditCourse(Long courseId, Integer auditStatus) {
        Course course = getById(courseId);
        if (course != null) {
            course.setAuditStatus(auditStatus);
            // 如果审核通过，自动上架
            if (auditStatus == 1) {
                course.setStatus(1);
            }
            course.setUpdateTime(LocalDateTime.now());
            updateById(course);
        }
    }
    
    @Override
    public List<Course> getTeacherCoursesWithAuditStatus(Long teacherId) {
        return list(new QueryWrapper<Course>().eq("teacher_id", teacherId).orderByDesc("create_time"));
    }
}