package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.UserFavorite;
import com.fafu.learning.vo.UserFavoriteVO;

import java.util.List;

public interface UserFavoriteService extends IService<UserFavorite> {
    
    boolean addFavorite(Long userId, Long courseId);
    
    boolean removeFavorite(Long userId, Long courseId);
    
    boolean isFavorite(Long userId, Long courseId);
    
    List<UserFavoriteVO> getUserFavorites(Long userId);
    
    int getUserFavoriteCount(Long userId);
}