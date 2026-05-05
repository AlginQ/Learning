package com.fafu.learning.vo;

import lombok.Data;

@Data
public class CourseCommentVO {
    
    private Long id;
    private Long courseId;
    private Long userId;
    private String username;
    private Integer rating;
    private String content;
    private Integer likes;
    private String createTime;
}