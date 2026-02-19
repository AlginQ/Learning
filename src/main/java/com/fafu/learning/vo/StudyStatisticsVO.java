package com.fafu.learning.vo;

import lombok.Data;

/**
 * 学习统计VO
 */
@Data
public class StudyStatisticsVO {
    
    /**
     * 总学习时长（小时）
     */
    private Double totalStudyTime;
    
    /**
     * 完成课时数
     */
    private Integer completedLessons;
    
    /**
     * 完成课程数
     */
    private Integer completedCourses;
    
    /**
     * 连续学习天数
     */
    private Integer currentStreak;
    
    /**
     * 总学习课程数
     */
    private Integer totalCourses;
    
    /**
     * 今日学习时长
     */
    private Double todayStudyTime;
    
    /**
     * 本周学习时长
     */
    private Double weekStudyTime;
    
    /**
     * 本月学习时长
     */
    private Double monthStudyTime;
}