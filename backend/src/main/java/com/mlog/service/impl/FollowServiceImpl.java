package com.mlog.service.impl;

import com.mlog.dto.Result;
import com.mlog.entity.Follow;
import com.mlog.entity.User;
import com.mlog.mapper.FollowMapper;
import com.mlog.service.IFollowService;
import com.mlog.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mlog.utils.RedisConstants.FOLLOW_USER_KEY;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements IFollowService {

    private final FollowMapper followMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        if (followUserId == null) {
            return Result.fail("팔로우 대상이 필요합니다.");
        }

        if (userId.equals(followUserId)) {
            return Result.fail("자기 자신은 팔로우할 수 없습니다.");
        }

        String key = FOLLOW_USER_KEY + userId;

        if (Boolean.TRUE.equals(isFollow)) {
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);

            int count = followMapper.countByUserIdAndFollowUserId(userId, followUserId);
            if (count > 0) {
                return Result.ok();
            }

            followMapper.insert(follow);
            stringRedisTemplate.opsForSet().add(key, followUserId.toString());

            return Result.ok();
        }

        followMapper.deleteByUserIdAndFollowUserId(userId, followUserId);
        stringRedisTemplate.opsForSet().remove(key, followUserId.toString());

        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        int count = followMapper.countByUserIdAndFollowUserId(userId, followUserId);

        return Result.ok(count > 0);
    }

    @Override
    public Result commonFollow(Long targetUserId) {
        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        String key1 = FOLLOW_USER_KEY + userId;
        String key2 = FOLLOW_USER_KEY + targetUserId;

        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key1, key2);

        if (intersect == null || intersect.isEmpty()) {
            return Result.ok(List.of());
        }

        String ids = intersect.stream()
                .filter(id -> id != null && id.matches("\\d+"))
                .collect(Collectors.joining(","));

        if (ids.isEmpty()) {
            return Result.ok(List.of());
        }

        List<User> users = followMapper.selectUsersByIds(ids);

        return Result.ok(users);
    }
}