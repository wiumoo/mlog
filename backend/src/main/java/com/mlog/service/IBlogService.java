package com.mlog.service;

import com.mlog.dto.BlogCreateRequest;
import com.mlog.dto.Result;

public interface IBlogService {

    Result createBlog(BlogCreateRequest request);

    Result getFeed();

    Result likeBlog(Long blogId);

    Result getHotFeed();

    Result getFollowFeed();

}
