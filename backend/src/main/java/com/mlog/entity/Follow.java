package com.mlog.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Follow {

    private Long id;

    // 팔로우를 한 사용자 ID
    private Long userId;

    // 팔로우 당한 사용자 ID
    private Long followUserId;

    private LocalDateTime createdAt;
}