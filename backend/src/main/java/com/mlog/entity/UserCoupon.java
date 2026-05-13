package com.mlog.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCoupon {

    private Long id;

    private Long eventId;

    private Long userId;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}