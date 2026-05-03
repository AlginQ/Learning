package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.UserFavorite;
import com.fafu.learning.vo.UserFavoriteVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    
    @Select("SELECT uf.id, uf.course_id as courseId, c.title as courseTitle, c.cover_image as courseCover, " +
            "c.price, c.discount_price as discountPrice, c.rating, c.student_count as studentCount, " +
            "cat.name as categoryName, t.name as teacherName, uf.create_time as createTime " +
            "FROM user_favorite uf " +
            "LEFT JOIN course c ON uf.course_id = c.id " +
            "LEFT JOIN category cat ON c.category_id = cat.id " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE uf.user_id = #{userId} " +
            "ORDER BY uf.create_time DESC")
    List<UserFavoriteVO> getUserFavorites(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND course_id = #{courseId}")
    int existsByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}