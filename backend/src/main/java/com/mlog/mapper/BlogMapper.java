package com.mlog.mapper;

import com.mlog.entity.Blog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BlogMapper {

    @Insert("""
            INSERT INTO blog (
                user_id,
                shop_id,
                title,
                content,
                image_url,
                liked_count,
                comments_count,
                status,
                created_at,
                updated_at
            )
            VALUES (
                #{userId},
                #{shopId},
                #{title},
                #{content},
                #{imageUrl},
                0,
                0,
                1,
                NOW(),
                NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Blog blog);

    @Select("""
            SELECT
                id,
                user_id AS userId,
                shop_id AS shopId,
                title,
                content,
                image_url AS imageUrl,
                liked_count AS likedCount,
                comments_count AS commentsCount,
                status,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM blog
            WHERE status = 1
            ORDER BY created_at DESC
            """)
    List<Blog> selectFeed();

    @Update("""
        UPDATE blog
        SET liked_count = liked_count + 1
        WHERE id = #{id}
        """)
    void incrementLikedCount(Long id);

    @Update("""
        UPDATE blog
        SET liked_count = liked_count - 1
        WHERE id = #{id}
          AND liked_count > 0
        """)
    void decrementLikedCount(Long id);
}