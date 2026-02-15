package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习进度实体类
 */
@Data
@TableName("study_progress")
public class StudyProgress {
    
    /**
     * 进度ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 课时ID
     */
    private Long lessonId;
    
    /**
     * 学习进度百分比 0-100
     */
    private Integer progress;
    
    /**
     * 已观看时长（秒）
     */
    private Integer watchDuration;
    
    /**
     * 是否完成 0-未完成 1-已完成
     */
    private Integer isCompleted;
    
    /**
     * 最后学习时间
     */
    private LocalDateTime lastStudyTime;
    
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