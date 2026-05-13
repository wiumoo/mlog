package com.mlog.service;

import com.mlog.dto.Result;

public interface IFollowService {

    /**
     * Follow or unfollow a user.
     *
     * @param followUserId target user id
     * @param isFollow true = follow, false = unfollow
     * @return result
     */
    Result follow(Long followUserId, Boolean isFollow);

    /**
     * Check whether current user follows target user.
     *
     * @param followUserId target user id
     * @return true or false
     */
    Result isFollow(Long followUserId);

    /**
     * Query common follows between current user and target user.
     *
     * @param targetUserId target user id
     * @return common follow users
     */
    Result commonFollow(Long targetUserId);
}