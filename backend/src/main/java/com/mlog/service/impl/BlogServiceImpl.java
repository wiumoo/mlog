package com.mlog.service.impl;

import com.mlog.dto.BlogCreateRequest;
import com.mlog.dto.Result;
import com.mlog.entity.Blog;
import com.mlog.mapper.BlogMapper;
import com.mlog.service.IBlogService;
import com.mlog.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements IBlogService {

    private final BlogMapper blogMapper;

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
}