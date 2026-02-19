package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.dto.UserLoginDTO;
import com.fafu.learning.dto.UserRegisterDTO;
import com.fafu.learning.entity.User;
import com.fafu.learning.mapper.UserMapper;
import com.fafu.learning.service.UserService;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.utils.PasswordUtil;
import com.fafu.learning.vo.LoginResponseVO;
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
    private UserMapper userMapper;
    
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
     * 注意：此接口不需要@PreAuthorize注解，已在SecurityConfig中配置为permitAll
     * JWT过滤器也会跳过对此接口的验证
     */
    @PostMapping("/login")
    public ApiResult<LoginResponseVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            LoginResponseVO loginVO = userService.login(loginDTO);
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
    
    /**
     * 用户登录接口 - 满足特定要求的实现
     * 1. 接收username和password参数
     * 2. 校验账号密码是否正确
     * 3. 从数据库获取role字段
     * 4. 生成包含userId和role的JWT Token
     * 5. 返回token、username、role字段
     */
    @PostMapping("/login-simple")
    public ApiResult<LoginResponseVO> simpleLogin(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            // 1. 接收前端传的username和password参数，校验账号密码是否正确
            User user = validateUserCredentials(loginDTO.getUsername(), loginDTO.getPassword());
            
            // 2. 校验通过后，从数据库user表中获取该用户的role字段
            String role = user.getRole();
            
            // 3. 生成包含userId和role的JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            
            // 4. 接口返回格式为Result对象，包含token、username、role字段
            LoginResponseVO response = new LoginResponseVO();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setRole(role);
            
            return ApiResult.success("登录成功", response);
            
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 校验用户凭证
     * @param username 用户名或邮箱
     * @param password 密码
     * @return User 用户实体
     * @throws RuntimeException 当凭证无效时抛出
     */
    private User validateUserCredentials(String username, String password) {
        // 根据用户名查找用户
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", username)
        );
        
        // 如果用户名没找到，尝试用邮箱查找
        if (user == null) {
            user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("email", username)
            );
        }
        
        // 检查用户是否存在
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 5. 密码校验使用BCryptPasswordEncoder
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        return user;
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public ApiResult<UserInfoVO> updateUserInfo(
            HttpServletRequest request,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    UserInfoVO userInfo = userService.updateUserInfo(userId, updateDTO);
                    return ApiResult.success("信息更新成功", userInfo);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 更新用户头像
     */
    @PostMapping("/avatar")
    public ApiResult<Void> updateUserAvatar(
            HttpServletRequest request,
            @RequestParam("avatarUrl") String avatarUrl) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    userService.updateUserAvatar(userId, avatarUrl);
                    return ApiResult.success("头像更新成功", null);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password")
    public ApiResult<Void> updatePassword(
            HttpServletRequest request,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    userService.updatePassword(userId, oldPassword, newPassword);
                    return ApiResult.success("密码修改成功", null);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest(e.getMessage());
        }
    }
}