package com.fafu.learning.vo;

import lombok.Data;

@Data
public class UserFavoriteVO {
    
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseCover;
    private Double price;
    private Double discountPrice;
    private Double rating;
    private Integer studentCount;
    private String categoryName;
    private String teacherName;
    private String createTime;
}