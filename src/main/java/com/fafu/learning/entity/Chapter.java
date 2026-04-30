package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程章节实体类
 */
@Data
@TableName("chapter")
public class Chapter {
    
    /**
     * 章节ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 章节标题
     */
    private String title;
    
    /**
     * 章节序号
     */
    private Integer chapterNumber;
    
    /**
     * 章节描述
     */
    private String description;
    
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
    
    /**
     * 课时列表（临时字段，不映射数据库）
     */
    @TableField(exist = false)
    private List<Lesson> lessons;
}