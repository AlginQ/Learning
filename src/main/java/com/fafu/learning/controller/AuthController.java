package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.dto.UserLoginDTO;
import com.fafu.learning.dto.UserRegisterDTO;
import com.fafu.learning.service.UserService;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.vo.LoginVO;
import com.fafu.learning.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResult<UserInfoVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        try {
            UserInfoVO userInfo = userService.register(registerDTO);
            return ApiResult.success("注册成功", userInfo);
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResult<LoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            LoginVO loginVO = userService.login(loginDTO);
            return ApiResult.success("登录成功", loginVO);
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ApiResult<UserInfoVO> getCurrentUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    UserInfoVO userInfo = userService.getCurrentUserInfo(userId);
                    return ApiResult.success(userInfo);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.unauthorized("Token无效");
        }
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        // JWT是无状态的，客户端删除token即可
        return ApiResult.success("退出成功", null);
    }
}