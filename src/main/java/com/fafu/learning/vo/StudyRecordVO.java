package com.fafu.learning.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习记录VO
 */
@Data
public class StudyRecordVO {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程标题
     */
    private String courseTitle;
    
    /**
     * 课时标题
     */
    private String lessonTitle;
    
    /**
     * 学习时长（分钟）
     */
    private Integer duration;
    
    /**
     * 学习进度百分比
     */
    private Integer progress;
    
    /**
     * 学习时间
     */
    private LocalDateTime studyTime;
    
    /**
     * 课程类别
     */
    private String category;
    
    /**
     * 课程难度
     */
    private String difficulty;
}