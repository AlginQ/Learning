package com.fafu.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 教师申请表
 */
@Data
@TableName("teacher_apply")
public class TeacherApply {
    
    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
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
    
    /**
     * 状态 0-待审核 1-通过 2-拒绝
     */
    private Integer status;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核时间
     */
    private Date reviewTime;
    
    /**
     * 审核备注
     */
    private String reviewRemark;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}