package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_progress")
public class StudyProgress {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("lesson_id")
    private Long lessonId;
    
    @TableField("progress")
    private Integer progress;
    
    @TableField("watch_duration")
    private Integer watchDuration;
    
    @TableField("is_completed")
    private Integer isCompleted;
    
    @TableField("last_study_time")
    private LocalDateTime lastStudyTime;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}