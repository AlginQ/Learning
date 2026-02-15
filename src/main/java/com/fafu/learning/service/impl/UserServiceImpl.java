package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.User;
import com.fafu.learning.mapper.UserMapper;
import com.fafu.learning.service.UserService;
import com.fafu.learning.dto.UserLoginDTO;
import com.fafu.learning.dto.UserRegisterDTO;
import com.fafu.learning.vo.LoginVO;
import com.fafu.learning.vo.UserInfoVO;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public UserInfoVO register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (getByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (getByEmail(registerDTO.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(PasswordUtil.encode(registerDTO.getPassword()));
        user.setRole("USER");
        user.setStatus(1);
        
        // 保存用户
        save(user);
        
        // 返回用户信息（不包含密码）
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    public LoginVO login(UserLoginDTO loginDTO) {
        // 根据用户名或邮箱查找用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            user = getByEmail(loginDTO.getUsername());
        }
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 更新最后登录时间
        updateLastLoginTime(user.getId());
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 构造返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        loginVO.setUserInfo(userInfoVO);
        
        return loginVO;
    }
    
    @Override
    public User getByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return getOne(wrapper);
    }
    
    @Override
    public User getByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return getOne(wrapper);
    }
    
    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
    }
}