package com.mlog.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Shop {

    private Long id;

    private String name;

    private String category;

    private String address;

    private String region;

    private String imageUrl;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer avgPrice;

    private String description;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Double latitude;

    private Double longitude;

    private Double distance;
}
