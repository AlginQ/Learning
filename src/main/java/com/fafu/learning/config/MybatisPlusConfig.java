package com.fafu.learning.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类
 */
@Configuration
@MapperScan("com.fafu.learning.mapper")
public class MybatisPlusConfig {
    // 基础配置已完成
}