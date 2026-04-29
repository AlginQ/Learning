package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.User;
import com.fafu.learning.mapper.UserMapper;
import com.fafu.learning.service.UserService;
import com.fafu.learning.dto.UserLoginDTO;
import com.fafu.learning.dto.UserRegisterDTO;
import com.fafu.learning.dto.UserUpdateDTO;
import com.fafu.learning.vo.LoginResponseVO;
import com.fafu.learning.vo.UserInfoVO;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 默认头像列表
     */
    private static final List<String> DEFAULT_AVATARS = Arrays.asList(
        "/images/4624.png",
        "/images/5251.png", 
        "/images/true.png"
    );
    
    /**
     * 获取默认头像（第一张图）
     */
    private String getDefaultAvatar() {
        return DEFAULT_AVATARS.get(0);
    }
    
    /**
     * 获取随机头像
     */
    private String getRandomAvatar() {
        Random random = new Random();
        return DEFAULT_AVATARS.get(random.nextInt(DEFAULT_AVATARS.size()));
    }
    
    @Override
    @Transactional
    public UserInfoVO register(UserRegisterDTO registerDTO) {
        // 检查用户名是否符合规则
        if (!registerDTO.getUsername().matches("^[a-zA-Z0-9_]{4,20}$")) {
            throw new RuntimeException("用户名只能包含字母、数字和下划线，长度4-20位");
        }
        
        // 检查密码是否符合规则
        String password = registerDTO.getPassword();
        if (password.length() < 6 || password.length() > 20) {
            throw new RuntimeException("密码长度必须为6-20位");
        }
        if (password.matches("^[\\u4e00-\\u9fa5]+$") || password.contains(" ")) {
            throw new RuntimeException("密码不允许包含中文和空格");
        }
        if (password.matches("^[a-zA-Z]+$")) {
            throw new RuntimeException("密码不允许纯字母");
        }
        
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
        // 新注册用户随机分配头像
        user.setAvatar(getRandomAvatar());
        
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
        
        System.out.println("获取用户信息，用户ID: " + userId);
        System.out.println("数据库中的头像: " + user.getAvatar());
        
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        
        System.out.println("复制后的头像: " + userInfoVO.getAvatar());
        
        // 如果用户没有头像，设置默认头像（第一张图）
        if (userInfoVO.getAvatar() == null || userInfoVO.getAvatar().isEmpty()) {
            System.out.println("头像为空，设置默认头像");
            userInfoVO.setAvatar(getDefaultAvatar());
        }
        
        System.out.println("最终返回的头像: " + userInfoVO.getAvatar());
        
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
        System.out.println("开始获取所有用户...");
        List<User> users = list();
        System.out.println("获取到的用户数量: " + users.size());
        for (User user : users) {
            System.out.println("用户: " + user.getUsername() + "，角色: " + user.getRole());
        }
        return users.stream()
            .map(user -> {
                UserInfoVO vo = new UserInfoVO();
                BeanUtils.copyProperties(user, vo);
                // 如果用户没有头像，设置默认头像（第一张图）
                if (vo.getAvatar() == null || vo.getAvatar().isEmpty()) {
                    vo.setAvatar(getDefaultAvatar());
                }
                return vo;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public void deleteUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        removeById(id);
    }
    
    @Override
    public UserInfoVO getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }
    
    @Override
    public void disableUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(0);
        updateById(user);
    }
    
    @Override
    public void enableUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(1);
        updateById(user);
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
    public String updateUserAvatar(Long userId, org.springframework.web.multipart.MultipartFile file) {
        System.out.println("开始处理头像上传，用户ID: " + userId);
        
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的头像文件");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        System.out.println("文件类型: " + contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("请上传有效的图片文件");
        }
        
        // 检查文件大小（最大2MB）
        System.out.println("文件大小: " + file.getSize() + " bytes");
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过2MB");
        }
        
        // 保存文件到静态资源目录（使用绝对路径）
        java.io.File uploadDir = new java.io.File("D:\\Javaperject\\Learning\\appui\\public\\images");
        System.out.println("上传目录: " + uploadDir.getAbsolutePath());
        System.out.println("上传目录是否存在: " + uploadDir.exists());
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("目录创建结果: " + created);
        }
        
        // ========== 删除旧头像文件（如果存在） ==========
        String oldAvatar = user.getAvatar();
        System.out.println("=== 开始处理旧头像删除 ===");
        System.out.println("旧头像路径: " + oldAvatar);
        System.out.println("默认头像列表: " + DEFAULT_AVATARS);
        
        if (oldAvatar != null && !oldAvatar.isEmpty()) {
            // 检查是否是默认头像（默认头像不删除）
            boolean isDefaultAvatar = DEFAULT_AVATARS.contains(oldAvatar);
            System.out.println("是否是默认头像: " + isDefaultAvatar);
            
            if (!isDefaultAvatar) {
                // 提取文件名（处理可能的路径格式）
                String oldFileName = oldAvatar;
                if (oldAvatar.startsWith("/images/")) {
                    oldFileName = oldAvatar.substring("/images/".length());
                } else if (oldAvatar.contains("/")) {
                    oldFileName = oldAvatar.substring(oldAvatar.lastIndexOf("/") + 1);
                }
                System.out.println("旧头像文件名: " + oldFileName);
                
                // 使用完整路径创建文件对象
                java.io.File oldFile = new java.io.File(uploadDir, oldFileName);
                System.out.println("旧头像文件完整路径: " + oldFile.getAbsolutePath());
                System.out.println("旧头像文件是否存在: " + oldFile.exists());
                
                if (oldFile.exists()) {
                    // 尝试强制删除
                    boolean deleted = oldFile.delete();
                    System.out.println("旧头像文件删除结果: " + deleted);
                    if (!deleted) {
                        // 尝试使用 Files.delete
                        try {
                            java.nio.file.Files.delete(oldFile.toPath());
                            System.out.println("使用 Files.delete 删除成功");
                        } catch (java.io.IOException e) {
                            System.err.println("警告：旧头像文件删除失败: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("旧头像文件不存在，无需删除");
                }
            } else {
                System.out.println("是默认头像，不删除");
            }
        } else {
            System.out.println("旧头像为空，无需删除");
        }
        System.out.println("=== 旧头像处理完成 ===");
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".png";
        String newFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;
        System.out.println("新文件名: " + newFilename);
        
        java.io.File destFile = new java.io.File(uploadDir, newFilename);
        System.out.println("目标文件: " + destFile.getAbsolutePath());
        
        try {
            file.transferTo(destFile);
            System.out.println("文件保存成功");
        } catch (java.io.IOException e) {
            System.err.println("文件保存失败: " + e.getMessage());
            throw new RuntimeException("头像上传失败: " + e.getMessage());
        }
        
        // 更新用户头像路径
        String avatarUrl = "/images/" + newFilename;
        user.setAvatar(avatarUrl);
        updateById(user);
        System.out.println("头像更新成功，URL: " + avatarUrl);
        
        return avatarUrl;
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
        
        // 检查新密码是否符合规则
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new RuntimeException("密码长度必须为6-20位");
        }
        if (newPassword.matches("^[\\u4e00-\\u9fa5]+$") || newPassword.contains(" ")) {
            throw new RuntimeException("密码不允许包含中文和空格");
        }
        if (newPassword.matches("^[a-zA-Z]+$")) {
            throw new RuntimeException("密码不允许纯字母");
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        updateById(user);
    }

    @Override
    public void updateUserRole(Long userId, String role) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRole(role);
        updateById(user);
    }
}