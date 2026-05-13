package com.mlog.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponEvent {

    private Long id;

    private String name;

    private Integer totalStock;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
