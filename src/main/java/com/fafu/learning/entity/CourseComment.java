package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_review")
public class CourseComment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("course_id")
    private Long courseId;
    
    @TableField("rating")
    private Integer rating;
    
    @TableField("content")
    private String content;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField("status")
    private Integer status;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}