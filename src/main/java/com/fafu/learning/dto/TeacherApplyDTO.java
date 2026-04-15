package com.fafu.learning.dto;

import lombok.Data;

/**
 * 教师申请DTO
 */
@Data
public class TeacherApplyDTO {
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 资质说明
     */
    private String qualification;
}