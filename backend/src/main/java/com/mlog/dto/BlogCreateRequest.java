package com.mlog.dto;

import lombok.Data;

@Data
public class BlogCreateRequest {

    private Long shopId;
    private String title;
    private String content;
    private String imageUrl;
}