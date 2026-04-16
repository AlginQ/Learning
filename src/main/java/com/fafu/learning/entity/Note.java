package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 笔记实体类
 */
@Data
@TableName("note")
public class Note {
    
    /**
     * 笔记ID
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
    @TableField(value = "course_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.ALWAYS)
    private Long courseId;
    
    /**
     * 课时ID
     */
    @TableField(value = "lesson_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.ALWAYS)
    private Long lessonId;
    
    /**
     * 笔记标题
     */
    private String title;
    
    /**
     * 笔记内容
     */
    private String content;
    
    /**
     * 笔记类型：0-课程笔记，1-通用笔记
     */
    private Integer type;
    
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