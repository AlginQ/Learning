package com.fafu.learning.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fafu.learning.common.ApiResult;
import com.fafu.learning.common.PageResult;
import com.fafu.learning.entity.Course;
import com.fafu.learning.entity.Lesson;
import com.fafu.learning.entity.Chapter;
import com.fafu.learning.entity.Category;
import com.fafu.learning.entity.User;
import com.fafu.learning.mapper.ChapterMapper;
import com.fafu.learning.mapper.LessonMapper;
import com.fafu.learning.mapper.CategoryMapper;
import com.fafu.learning.mapper.UserMapper;
import com.fafu.learning.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private LessonMapper lessonMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取课程列表
     */
    @GetMapping
    public ApiResult<PageResult<Course>> getCourseList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "12") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        try {
            System.out.println("Page: " + page + ", Size: " + size + ", Keyword: " + keyword + ", Status: " + status);
            
            // 构建查询条件
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            
            // 如果指定了status，则按status过滤；否则默认只显示上架课程
            if (status != null) {
                queryWrapper.eq("status", status);
            } else {
                queryWrapper.eq("status", 1);
            }
            
            // 获取课程列表（审核状态为通过）
            List<Course> allCourses = courseService.list(queryWrapper
                .eq("audit_status", 1)
                .orderByDesc("create_time"));
            
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
            
            // 根据categoryId过滤课程
            if (categoryId != null) {
                filteredCourses = filteredCourses.stream()
                    .filter(course -> categoryId.equals(course.getCategoryId()))
                    .collect(java.util.stream.Collectors.toList());
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
     * 获取所有审核通过的课程（管理员专用，包含下架课程）
     */
    @GetMapping("/all")
    public ApiResult<PageResult<Course>> getAllApprovedCourses(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "12") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            System.out.println("Admin - Page: " + page + ", Size: " + size + ", Keyword: " + keyword + ", Status: " + status);
            
            // 构建查询条件
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            
            // 只显示审核通过的课程
            queryWrapper.eq("audit_status", 1);
            
            // 如果指定了status，则按status过滤
            if (status != null) {
                queryWrapper.eq("status", status);
            }
            
            // 按关键词搜索
            if (keyword != null && !keyword.isEmpty()) {
                queryWrapper.like("title", keyword);
            }
            
            // 按创建时间倒序
            queryWrapper.orderByDesc("create_time");
            
            // 执行分页查询
            Page<Course> pageParam = new Page<>(page, size);
            Page<Course> result = courseService.page(pageParam, queryWrapper);
            
            PageResult<Course> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
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
     * 获取待审核课程列表
     */
    @GetMapping("/pending")
    public ApiResult<List<Course>> getPendingCourses() {
        try {
            List<Course> courses = courseService.getPendingCourses();
            return ApiResult.success(courses);
        } catch (Exception e) {
            return ApiResult.fail("获取待审核课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取教师课程列表（包含审核状态）
     */
    @GetMapping("/teacher/{teacherId}")
    public ApiResult<List<Course>> getTeacherCourses(@PathVariable Long teacherId) {
        try {
            List<Course> courses = courseService.getTeacherCoursesWithAuditStatus(teacherId);
            return ApiResult.success(courses);
        } catch (Exception e) {
            return ApiResult.fail("获取教师课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public ApiResult<java.util.Map<String, Object>> getCourseDetail(@PathVariable Long id) {
        try {
            Course course = courseService.getById(id);
            if (course == null) {
                return ApiResult.notFound("课程不存在");
            }
            
            // 创建响应对象
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", course.getId());
            result.put("title", course.getTitle());
            result.put("description", course.getDescription());
            result.put("coverImage", course.getCoverImage());
            result.put("introduction", course.getIntroduction());
            result.put("price", course.getPrice());
            result.put("discountPrice", course.getDiscountPrice());
            result.put("lessonCount", course.getLessonCount());
            result.put("studentCount", course.getStudentCount());
            result.put("rating", course.getRating());
            result.put("createTime", course.getCreateTime());
            
            // 获取分类信息
            Category category = categoryMapper.selectById(course.getCategoryId());
            java.util.Map<String, Object> categoryMap = new java.util.HashMap<>();
            if (category != null) {
                categoryMap.put("id", category.getId());
                categoryMap.put("name", category.getName() != null ? category.getName() : "未分类");
            } else {
                categoryMap.put("id", course.getCategoryId());
                categoryMap.put("name", "未分类");
            }
            result.put("category", categoryMap);
            
            // 获取教师信息
            User teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                java.util.Map<String, Object> teacherMap = new java.util.HashMap<>();
                teacherMap.put("id", teacher.getId());
                teacherMap.put("name", teacher.getNickname());
                teacherMap.put("avatar", teacher.getAvatar());
                teacherMap.put("title", teacher.getTitle());
                teacherMap.put("introduction", teacher.getIntroduction());
                result.put("teacher", teacherMap);
            }
            
            return ApiResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail("获取课程详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加课程
     */
    @PostMapping
    public ApiResult<Course> addCourse(@RequestBody java.util.Map<String, Object> requestBody) {
        try {
            // 调试日志：检查前端传递的原始数据
            System.out.println("=== 课程创建请求 ===");
            System.out.println("原始请求体: " + requestBody);
            System.out.println("课程标题: " + requestBody.get("title"));
            System.out.println("章节列表: " + requestBody.get("lessons"));
            
            // 将Map转换为Course对象
            Course course = new Course();
            course.setTitle((String) requestBody.get("title"));
            course.setDescription((String) requestBody.get("description"));
            course.setCoverImage((String) requestBody.get("coverImage"));
            course.setPrice(requestBody.get("price") != null ? new java.math.BigDecimal(requestBody.get("price").toString()) : java.math.BigDecimal.ZERO);
            course.setDiscountPrice(requestBody.get("discountPrice") != null ? new java.math.BigDecimal(requestBody.get("discountPrice").toString()) : java.math.BigDecimal.ZERO);
            course.setCategoryId(requestBody.get("categoryId") != null ? Long.parseLong(requestBody.get("categoryId").toString()) : 1L);
            course.setTeacherId(requestBody.get("teacherId") != null ? Long.parseLong(requestBody.get("teacherId").toString()) : 1L);
            course.setLessonCount(requestBody.get("lessonCount") != null ? Integer.parseInt(requestBody.get("lessonCount").toString()) : 0);
            course.setIntroduction((String) requestBody.get("introduction"));
            course.setStatus(0);
            course.setAuditStatus(0);
            
            // 处理章节数据
            java.util.List<Lesson> lessons = new java.util.ArrayList<>();
            Object lessonsObj = requestBody.get("lessons");
            if (lessonsObj instanceof java.util.List) {
                for (Object lessonObj : (java.util.List<?>) lessonsObj) {
                    if (lessonObj instanceof java.util.Map) {
                        java.util.Map<String, Object> lessonMap = (java.util.Map<String, Object>) lessonObj;
                        Lesson lesson = new Lesson();
                        lesson.setTitle((String) lessonMap.get("title"));
                        lesson.setVideoUrl((String) lessonMap.get("videoUrl"));
                        lesson.setDuration(lessonMap.get("duration") != null ? Integer.parseInt(lessonMap.get("duration").toString()) : 0);
                        lesson.setLessonNumber(lessonMap.get("lessonNumber") != null ? Integer.parseInt(lessonMap.get("lessonNumber").toString()) : 0);
                        lessons.add(lesson);
                    }
                }
            }
            course.setLessons(lessons);
            System.out.println("解析后的章节数量: " + lessons.size());
            
            Course createdCourse = courseService.createCourse(course);
            return ApiResult.success("课程添加成功", createdCourse);
        } catch (Exception e) {
            e.printStackTrace();
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
    
    /**
     * 审核课程
     */
    @PutMapping("/{id}/audit")
    public ApiResult<Void> auditCourse(@PathVariable Long id, @RequestBody Map<String, Integer> requestBody) {
        try {
            Integer status = requestBody.get("status");
            courseService.auditCourse(id, status);
            String message = status == 1 ? "课程审核通过" : "课程审核拒绝";
            return ApiResult.success(message, null);
        } catch (Exception e) {
            return ApiResult.fail("审核课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程章节和课时
     */
    @GetMapping("/{id}/chapters")
    public ApiResult<List<Chapter>> getCourseChapters(@PathVariable Long id) {
        try {
            // 获取课程的所有章节
            List<Chapter> chapters = chapterMapper.selectList(new QueryWrapper<Chapter>()
                .eq("course_id", id)
                .orderByAsc("chapter_number"));
            
            // 为每个章节加载课时
            for (Chapter chapter : chapters) {
                List<Lesson> lessons = lessonMapper.selectList(new QueryWrapper<Lesson>()
                    .eq("chapter_id", chapter.getId())
                    .orderByAsc("lesson_number"));
                chapter.setLessons(lessons);
            }
            
            return ApiResult.success(chapters);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail("获取课程章节失败: " + e.getMessage());
        }
    }
}