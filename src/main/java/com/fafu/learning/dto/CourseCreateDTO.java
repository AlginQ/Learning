package com.fafu.learning.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程创建请求DTO
 */
@Data
public class CourseCreateDTO {
    
    private String title;
    
    private String description;
    
    private String coverImage;
    
    private BigDecimal price;
    
    private BigDecimal discountPrice;
    
    private Long categoryId;
    
    private Long teacherId;
    
    private Integer lessonCount;
    
    private String introduction;
    
    private Integer status;
    
    private Integer auditStatus;
    
    /**
     * 章节列表
     */
    private List<LessonDTO> lessons;
    
    /**
     * 课时信息DTO
     */
    @Data
    public static class LessonDTO {
        private String title;
        private String videoUrl;
        private Integer duration;
        private Integer lessonNumber;
    }
}