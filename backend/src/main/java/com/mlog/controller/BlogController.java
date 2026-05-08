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

    @PostMapping("/{id}/like")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    @GetMapping("/feed/hot")
    public Result getHotFeed() {
        return blogService.getHotFeed();
    }

    @GetMapping("/feed/follow")
    public Result getFollowFeed() {
        return blogService.getFollowFeed();
    }

    @PostMapping("/{id}/visit")
    public Result recordVisit(@PathVariable("id") Long id) {
        return blogService.recordVisit(id);
    }

    @GetMapping("/{id}/visit/count")
    public Result getVisitCount(@PathVariable("id") Long id) {
        return blogService.getVisitCount(id);
    }

    @PostMapping("/visit/stats/save")
    public Result saveVisitStats() {
        return blogService.saveTodayVisitStats();
    }

}