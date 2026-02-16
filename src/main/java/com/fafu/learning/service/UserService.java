package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.User;
import com.fafu.learning.dto.UserLoginDTO;
import com.fafu.learning.dto.UserRegisterDTO;
import com.fafu.learning.vo.LoginResponseVO;
import com.fafu.learning.vo.UserInfoVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    UserInfoVO register(UserRegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    LoginResponseVO login(UserLoginDTO loginDTO);
    
    /**
     * 根据用户名获取用户
     */
    User getByUsername(String username);
    
    /**
     * 根据邮箱获取用户
     */
    User getByEmail(String email);
    
    /**
     * 获取当前登录用户信息
     */
    UserInfoVO getCurrentUserInfo(Long userId);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);
    
    /**
     * 获取所有用户（管理员功能）
     */
    List<UserInfoVO> getAllUsers();
    
    /**
     * 根据ID删除用户（管理员功能）
     */
    void deleteUserById(Long id);
    
    /**
     * 根据ID获取用户详情（管理员功能）
     */
    UserInfoVO getUserById(Long id);
    
    /**
     * 禁用用户（管理员功能）
     */
    void disableUser(Long id);
    
    /**
     * 启用用户（管理员功能）
     */
    void enableUser(Long id);
}