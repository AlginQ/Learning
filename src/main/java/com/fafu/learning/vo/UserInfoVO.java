package com.fafu.learning.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 简介
     */
    private String introduction;
    
    /**
     * 专业
     */
    private String specialty;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
}