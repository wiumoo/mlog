package com.mlog.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;

    private String phone;

    private String nickname;

    private String avatar;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;
}
