package com.fafu.learning.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 * 用于生成测试用户的加密密码
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成管理员密码 (admin123)
        String adminPassword = encoder.encode("admin123");
        System.out.println("管理员密码 (admin123): " + adminPassword);
        
        // 生成测试用户密码 (user123)
        String userPassword = encoder.encode("user123");
        System.out.println("测试用户密码 (user123): " + userPassword);
    }
}