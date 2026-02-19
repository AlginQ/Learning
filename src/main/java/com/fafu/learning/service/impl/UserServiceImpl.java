package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.User;
import com.fafu.learning.mapper.UserMapper;
import com.fafu.learning.service.UserService;
import com.fafu.learning.dto.UserLoginDTO;
import com.fafu.learning.dto.UserRegisterDTO;
import com.fafu.learning.vo.LoginResponseVO;
import com.fafu.learning.vo.UserInfoVO;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public LoginResponseVO login(UserLoginDTO loginDTO) {
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
        LoginResponseVO loginVO = new LoginResponseVO();
        loginVO.setToken(token);
        
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(user.getRole());
        
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
    
    @Override
    public List<UserInfoVO> getAllUsers() {
        // TODO: 实现获取所有用户逻辑
        // List<User> users = list();
        // return users.stream()
        //     .map(user -> {
        //         UserInfoVO vo = new UserInfoVO();
        //         BeanUtils.copyProperties(user, vo);
        //         return vo;
        //     })
        //     .collect(Collectors.toList());
        return List.of();
    }
    
    @Override
    public void deleteUserById(Long id) {
        // TODO: 实现删除用户逻辑
        // User user = getById(id);
        // if (user == null) {
        //     throw new RuntimeException("用户不存在");
        // }
        // removeById(id);
    }
    
    @Override
    public UserInfoVO getUserById(Long id) {
        // TODO: 实现根据ID获取用户详情逻辑
        // User user = getById(id);
        // if (user == null) {
        //     throw new RuntimeException("用户不存在");
        // }
        // UserInfoVO userInfoVO = new UserInfoVO();
        // BeanUtils.copyProperties(user, userInfoVO);
        // return userInfoVO;
        return null;
    }
    
    @Override
    public void disableUser(Long id) {
        // TODO: 实现禁用用户逻辑
        // User user = getById(id);
        // if (user == null) {
        //     throw new RuntimeException("用户不存在");
        // }
        // user.setStatus(0);
        // updateById(user);
    }
    
    @Override
    public void enableUser(Long id) {
        // TODO: 实现启用用户逻辑
        // User user = getById(id);
        // if (user == null) {
        //     throw new RuntimeException("用户不存在");
        // }
        // user.setStatus(1);
        // updateById(user);
    }
    
    @Override
    public UserInfoVO updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查邮箱是否被其他用户使用
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            User existingUser = getByEmail(updateDTO.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        // 检查手机号是否被其他用户使用
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(user.getPhone())) {
            QueryWrapper<User> phoneQuery = new QueryWrapper<>();
            phoneQuery.eq("phone", updateDTO.getPhone());
            User existingUser = getOne(phoneQuery);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
            user.setPhone(updateDTO.getPhone());
        }
        
        // 更新其他字段
        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }
        if (updateDTO.getBirthday() != null) {
            user.setBirthday(updateDTO.getBirthday());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        
        updateById(user);
        
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    public void updateUserAvatar(Long userId, String avatarUrl) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setAvatar(avatarUrl);
        updateById(user);
    }
    
    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        updateById(user);
    }
}