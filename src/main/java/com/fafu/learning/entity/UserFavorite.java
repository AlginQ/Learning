package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("course_id")
    private Long courseId;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}