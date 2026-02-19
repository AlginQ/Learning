package com.fafu.learning.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户信息更新DTO
 */
@Data
public class UserUpdateDTO {
    
    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50位")
    private String nickname;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号格式不正确")
    private String phone;
    
    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDateTime birthday;
    
    /**
     * 头像URL
     */
    private String avatar;
}