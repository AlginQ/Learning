package com.fafu.learning.controller;

import com.fafu.learning.common.ApiResult;
import com.fafu.learning.service.UserFavoriteService;
import com.fafu.learning.utils.JwtUtil;
import com.fafu.learning.vo.UserFavoriteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class UserFavoriteController {
    
    @Autowired
    private UserFavoriteService favoriteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add/{courseId}")
    public ApiResult<Map<String, Object>> addFavorite(HttpServletRequest request, @PathVariable Long courseId) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    boolean success = favoriteService.addFavorite(userId, courseId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", success);
                    result.put("isFavorite", true);
                    return ApiResult.success(result);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest("添加收藏失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/remove/{courseId}")
    public ApiResult<Map<String, Object>> removeFavorite(HttpServletRequest request, @PathVariable Long courseId) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    boolean success = favoriteService.removeFavorite(userId, courseId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", success);
                    result.put("isFavorite", false);
                    return ApiResult.success(result);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest("取消收藏失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{courseId}")
    public ApiResult<Map<String, Object>> getFavoriteStatus(HttpServletRequest request, @PathVariable Long courseId) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    boolean isFavorite = favoriteService.isFavorite(userId, courseId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("isFavorite", isFavorite);
                    return ApiResult.success(result);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest("获取收藏状态失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public ApiResult<List<UserFavoriteVO>> getUserFavorites(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    List<UserFavoriteVO> favorites = favoriteService.getUserFavorites(userId);
                    return ApiResult.success(favorites);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest("获取收藏列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/count")
    public ApiResult<Map<String, Object>> getFavoriteCount(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    int count = favoriteService.getUserFavoriteCount(userId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("count", count);
                    return ApiResult.success(result);
                }
            }
            return ApiResult.unauthorized("未授权");
        } catch (Exception e) {
            return ApiResult.badRequest("获取收藏数量失败: " + e.getMessage());
        }
    }
}