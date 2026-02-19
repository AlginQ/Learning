package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习记录实体类
 */
@Data
@TableName("study_record")
public class StudyRecord {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课时ID
     */
    private Long lessonId;
    
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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}