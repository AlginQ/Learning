package com.fafu.learning.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus自动填充配置
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充courseId和lessonId，避免外键约束
        if (metaObject.hasSetter("courseId") && metaObject.getValue("courseId") == null) {
            this.strictInsertFill(metaObject, "courseId", Long.class, 25L); // 使用实际存在的课程ID
        }
        if (metaObject.hasSetter("lessonId") && metaObject.getValue("lessonId") == null) {
            this.strictInsertFill(metaObject, "lessonId", Long.class, 9L); // 使用实际存在的课时ID
        }
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}