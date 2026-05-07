package com.mlog.service.impl;

import com.mlog.dto.BlogCreateRequest;
import com.mlog.dto.Result;
import com.mlog.entity.Blog;
import com.mlog.mapper.BlogMapper;
import com.mlog.service.IBlogService;
import com.mlog.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mlog.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.mlog.utils.RedisConstants.BLOG_LIKED_RANK_KEY;
import static com.mlog.utils.RedisConstants.FOLLOW_USER_KEY;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements IBlogService {

    private final BlogMapper blogMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Result createBlog(BlogCreateRequest request) {

        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return Result.fail("제목을 입력해주세요.");
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            return Result.fail("내용을 입력해주세요.");
        }

        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setShopId(request.getShopId());
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setImageUrl(request.getImageUrl());

        blogMapper.insert(blog);

        return Result.ok(blog.getId());
    }

    @Override
    public Result getFeed() {
        return Result.ok(blogMapper.selectFeed());
    }

    @Override
    public Result likeBlog(Long blogId) {

        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        String key = BLOG_LIKED_KEY + blogId;
        String userIdStr = userId.toString();
        String blogIdStr = blogId.toString();

        Double score = stringRedisTemplate.opsForZSet().score(key, userIdStr);

        if (score == null) {
            // 좋아요 추가
            stringRedisTemplate.opsForZSet()
                    .add(key, userIdStr, System.currentTimeMillis());

            stringRedisTemplate.opsForZSet()
                    .incrementScore(BLOG_LIKED_RANK_KEY, blogIdStr, 1);

            blogMapper.incrementLikedCount(blogId);

            return Result.ok("liked");
        } else {
            // 좋아요 취소
            stringRedisTemplate.opsForZSet()
                    .remove(key, userIdStr);

            Double newScore = stringRedisTemplate.opsForZSet()
                    .incrementScore(BLOG_LIKED_RANK_KEY, blogIdStr, -1);

            if (newScore == null || newScore <= 0) {
                stringRedisTemplate.opsForZSet()
                        .remove(BLOG_LIKED_RANK_KEY, blogIdStr);
            }

            blogMapper.decrementLikedCount(blogId);

            return Result.ok("unliked");
        }
    }

    @Override
    public Result getHotFeed() {

        Set<String> blogIdSet = stringRedisTemplate.opsForZSet()
                .reverseRange(BLOG_LIKED_RANK_KEY, 0, 9);

        if (blogIdSet == null || blogIdSet.isEmpty()) {
            return Result.ok(blogMapper.selectFeed());
        }

        List<Blog> blogs = new ArrayList<>();

        for (String blogIdStr : blogIdSet) {
            Long blogId = Long.valueOf(blogIdStr);
            Blog blog = blogMapper.selectById(blogId);

            if (blog != null) {
                blogs.add(blog);
            }
        }

        return Result.ok(blogs);
    }

    @Override
    public Result getFollowFeed() {

        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        String key = FOLLOW_USER_KEY + userId;

        Set<String> followUserIdSet = stringRedisTemplate.opsForSet().members(key);

        if (followUserIdSet == null || followUserIdSet.isEmpty()) {
            return Result.ok(List.of());
        }

        String ids = followUserIdSet.stream()
                .filter(id -> id != null && id.matches("\\d+"))
                .collect(Collectors.joining(","));

        if (ids.isEmpty()) {
            return Result.ok(List.of());
        }

        List<Blog> blogs = blogMapper.selectFollowFeed(ids);

        return Result.ok(blogs);
    }


}