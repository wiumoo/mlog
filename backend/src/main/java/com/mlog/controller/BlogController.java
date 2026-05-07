package com.mlog.controller;

import com.mlog.dto.BlogCreateRequest;
import com.mlog.dto.Result;
import com.mlog.service.IBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final IBlogService blogService;

    @PostMapping
    public Result createBlog(@RequestBody BlogCreateRequest request) {
        return blogService.createBlog(request);
    }

    @GetMapping("/feed")
    public Result getFeed() {
        return blogService.getFeed();
    }
}