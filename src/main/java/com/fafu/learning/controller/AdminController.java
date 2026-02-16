package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.entity.User;
import com.fafu.learning.service.UserService;
import com.fafu.learning.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 * 提供管理员专属的用户管理功能
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户 - 仅ADMIN角色可访问
     * GET /api/admin/users
     *
     * @return ApiResult<List<UserInfoVO>> 用户列表
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<UserInfoVO>> getAllUsers() {
        try {
            // TODO: 调用UserService获取所有用户列表
            // List<UserInfoVO> users = userService.getAllUsers();
            // return ApiResult.success(users);
            
            // 示例返回（实际应调用Service层）
            return ApiResult.success("获取用户列表成功", List.of());
        } catch (Exception e) {
            return ApiResult.fail("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除指定ID的用户 - 仅ADMIN角色可访问
     * DELETE /api/admin/users/{id}
     *
     * @param id 用户ID
     * @return ApiResult<Void> 删除结果
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> deleteUser(@PathVariable Long id) {
        try {
            // TODO: 调用UserService删除用户
            // userService.deleteUserById(id);
            // return ApiResult.success("用户删除成功", null);
            
            // 示例返回（实际应调用Service层）
            return ApiResult.success("用户删除成功", null);
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户详情 - 仅ADMIN角色可访问
     * GET /api/admin/users/{id}
     *
     * @param id 用户ID
     * @return ApiResult<UserInfoVO> 用户详情
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<UserInfoVO> getUserById(@PathVariable Long id) {
        try {
            // TODO: 调用UserService根据ID获取用户详情
            // UserInfoVO user = userService.getUserById(id);
            // return ApiResult.success(user);
            
            // 示例返回（实际应调用Service层）
            return ApiResult.success("获取用户详情成功", null);
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 禁用用户账户 - 仅ADMIN角色可访问
     * PUT /api/admin/users/{id}/disable
     *
     * @param id 用户ID
     * @return ApiResult<Void> 操作结果
     */
    @PutMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> disableUser(@PathVariable Long id) {
        try {
            // TODO: 调用UserService禁用用户
            // userService.disableUser(id);
            // return ApiResult.success("用户禁用成功", null);
            
            // 示例返回（实际应调用Service层）
            return ApiResult.success("用户禁用成功", null);
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("禁用用户失败: " + e.getMessage());
        }
    }

    /**
     * 启用用户账户 - 仅ADMIN角色可访问
     * PUT /api/admin/users/{id}/enable
     *
     * @param id 用户ID
     * @return ApiResult<Void> 操作结果
     */
    @PutMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> enableUser(@PathVariable Long id) {
        try {
            // TODO: 调用UserService启用用户
            // userService.enableUser(id);
            // return ApiResult.success("用户启用成功", null);
            
            // 示例返回（实际应调用Service层）
            return ApiResult.success("用户启用成功", null);
        } catch (RuntimeException e) {
            return ApiResult.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail("启用用户失败: " + e.getMessage());
        }
    }
}