package com.mlog.mapper;

import com.mlog.entity.Follow;
import com.mlog.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {

    @Insert("""
            INSERT INTO follow (user_id, follow_user_id, created_at)
            VALUES (#{userId}, #{followUserId}, NOW())
            """)
    int insert(Follow follow);

    @Delete("""
            DELETE FROM follow
            WHERE user_id = #{userId}
              AND follow_user_id = #{followUserId}
            """)
    int deleteByUserIdAndFollowUserId(@Param("userId") Long userId,
                                      @Param("followUserId") Long followUserId);

    @Select("""
            SELECT COUNT(*)
            FROM follow
            WHERE user_id = #{userId}
              AND follow_user_id = #{followUserId}
            """)
    int countByUserIdAndFollowUserId(@Param("userId") Long userId,
                                     @Param("followUserId") Long followUserId);

    @Select("""
            SELECT id, phone, nickname, avatar, created_at AS createdAt, updated_at AS updateAt
            FROM user
            WHERE id IN (${ids})
            """)
    List<User> selectUsersByIds(@Param("ids") String ids);
}