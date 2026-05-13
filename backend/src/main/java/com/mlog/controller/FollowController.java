package com.mlog.controller;

import com.mlog.dto.Result;
import com.mlog.service.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final IFollowService followService;

    /**
     * Follow or unfollow a user.
     *
     * Example:
     * PUT /follow/2/true   -> follow user 2
     * PUT /follow/2/false  -> unfollow user 2
     */
    @PutMapping("/{followUserId}/{isFollow}")
    public Result follow(@PathVariable Long followUserId,
                         @PathVariable Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    /**
     * Check whether current user follows target user.
     *
     * Example:
     * GET /follow/or/not/2
     */
    @GetMapping("/or/not/{followUserId}")
    public Result isFollow(@PathVariable Long followUserId) {
        return followService.isFollow(followUserId);
    }

    /**
     * Query common follows between current user and target user.
     *
     * Example:
     * GET /follow/common/2
     */
    @GetMapping("/common/{targetUserId}")
    public Result commonFollow(@PathVariable Long targetUserId) {
        return followService.commonFollow(targetUserId);
    }
}