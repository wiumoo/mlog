package com.mlog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponClaimMessage {

    private Long eventId;

    private Long userId;

    private String requestId;

    private LocalDateTime createdAt;
}