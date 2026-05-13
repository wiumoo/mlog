package com.mlog.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BlogVisitStats {

    private Long id;

    private Long blogId;

    private LocalDate statDate;

    private Long uv;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
