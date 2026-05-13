package com.mlog.mapper;

import com.mlog.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCouponMapper {

    void insert(UserCoupon userCoupon);
}