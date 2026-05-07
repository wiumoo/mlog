package com.mlog.mapper;

import com.mlog.entity.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopMapper {
    // Query active shops only and order by newest first
    @Select("""
            SELECT *
            FROM shop
            WHERE status = 1
            ORDER BY id DESC
            """)
    List<Shop> findAll();
    // Query one active shop by shop id
    @Select("""
            SELECT *
            FROM shop
            WHERE id = #{id}
              AND status = 1
            """)
    Shop findById(@Param("id") Long id);

}
