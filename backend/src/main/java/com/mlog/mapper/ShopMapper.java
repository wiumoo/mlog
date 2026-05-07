package com.mlog.mapper;

import com.mlog.entity.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<Shop> findAll();

    Shop findById(@Param("id") Long id);

    List<Shop> selectAllWithLocation();

    List<Shop> selectByIds(@Param("ids") List<Long> ids);
}
