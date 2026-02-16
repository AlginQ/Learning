package com.fafu.learning.vo;

import lombok.Data;

/**
 * 登录响应VO - 包含token、username、role字段
 */
@Data
public class LoginResponseVO {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色 (ADMIN/USER)
     */
    private String role;
}