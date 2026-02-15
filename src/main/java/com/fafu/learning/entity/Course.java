package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course {
    
    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程标题
     */
    private String title;
    
    /**
     * 课程简介
     */
    private String description;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 课程详细介绍
     */
    private String introduction;
    
    /**
     * 学习要求 (JSON格式)
     */
    private String requirements;
    
    /**
     * 目标受众 (JSON格式)
     */
    private String targetAudience;
    
    /**
     * 原价
     */
    private BigDecimal price;
    
    /**
     * 折扣价
     */
    private BigDecimal discountPrice;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 讲师ID
     */
    private Long teacherId;
    
    /**
     * 课时总数
     */
    private Integer lessonCount;
    
    /**
     * 学习人数
     */
    private Integer studentCount;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;
    
    /**
     * 是否推荐 0-否 1-是
     */
    private Integer recommend;
    
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