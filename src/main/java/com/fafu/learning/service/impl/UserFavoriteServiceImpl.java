package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.UserFavorite;
import com.fafu.learning.mapper.UserFavoriteMapper;
import com.fafu.learning.service.UserFavoriteService;
import com.fafu.learning.vo.UserFavoriteVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {
    
    @Override
    public boolean addFavorite(Long userId, Long courseId) {
        if (isFavorite(userId, courseId)) {
            return true;
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setCourseId(courseId);
        favorite.setCreateTime(LocalDateTime.now());
        return save(favorite);
    }
    
    @Override
    public boolean removeFavorite(Long userId, Long courseId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("course_id", courseId);
        return remove(wrapper);
    }
    
    @Override
    public boolean isFavorite(Long userId, Long courseId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("course_id", courseId);
        return count(wrapper) > 0;
    }
    
    @Override
    public List<UserFavoriteVO> getUserFavorites(Long userId) {
        return baseMapper.getUserFavorites(userId);
    }
    
    @Override
    public int getUserFavoriteCount(Long userId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return Math.toIntExact(count(wrapper));
    }
}