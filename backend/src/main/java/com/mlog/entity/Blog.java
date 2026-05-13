package com.mlog.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Blog {

    private Long id;
    private Long userId;
    private Long shopId;

    private String title;
    private String content;
    private String imageUrl;

    private Integer likedCount;
    private Integer commentsCount;
    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
