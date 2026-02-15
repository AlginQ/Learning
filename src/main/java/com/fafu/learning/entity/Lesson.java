package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课时实体类
 */
@Data
@TableName("lesson")
public class Lesson {
    
    /**
     * 课时ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课时标题
     */
    private String title;
    
    /**
     * 课时描述
     */
    private String description;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 视频时长（秒）
     */
    private Integer duration;
    
    /**
     * 课时序号
     */
    private Integer lessonNumber;
    
    /**
     * 是否免费 0-收费 1-免费
     */
    private Integer isFree;
    
    /**
     * 课时内容（富文本）
     */
    private String content;
    
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