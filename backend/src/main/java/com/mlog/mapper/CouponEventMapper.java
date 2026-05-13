package com.mlog.mapper;

import com.mlog.entity.CouponEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponEventMapper {

    CouponEvent findById(@Param("id") Long id);
}
