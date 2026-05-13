package com.mlog.mapper;

import com.mlog.entity.BlogVisitStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface BlogVisitStatsMapper {

    void upsertDailyStats(@Param("blogId") Long blogId,
                          @Param("statDate") LocalDate statDate,
                          @Param("uv") Long uv);

    BlogVisitStats findByBlogIdAndDate(@Param("blogId") Long blogId,
                                       @Param("statDate") LocalDate statDate);
}