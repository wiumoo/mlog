package com.mlog.service.impl;

import com.mlog.dto.LoginRequest;
import com.mlog.dto.LoginResponse;
import com.mlog.dto.Result;
import com.mlog.entity.User;
import com.mlog.mapper.UserMapper;
import com.mlog.service.IUserService;
import com.mlog.utils.JwtUtils;
import com.mlog.utils.RandomUtils;
import com.mlog.utils.RedisConstants;
import com.mlog.utils.RegexUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.mlog.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.mlog.utils.RedisConstants.LOGIN_CODE_TTL;
import static com.mlog.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private final JwtUtils jwtUtils;

    @Override
    public Result sendCode(String phone) {
        //1. Validate phone number format
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("Invalid phone number format");
        }

        //2. Generate verification code
        String code = RandomUtils.randomCode6();

        //3. Store verification code in Redis with TTL(5 minutes)
        stringRedisTemplate.opsForValue()
                .set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        //4. Send the code
        log.info("sned verification code:{} ",code);

        return Result.ok();
    }

    @Override
    public Result login(LoginRequest request) {

        String phone = request.getPhone();
        String code = request.getCode();


        //1. Validate phone number format
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("Invalid phone number format");
        }

        //2. Validate code is not empty
        if (code == null || code.isEmpty()) {
            return Result.fail("Verification code required");
        }

        //3. Get code from Redis
        String cacheCode = stringRedisTemplate.opsForValue()
                .get(LOGIN_CODE_KEY + phone);

        //4. Check if code exist(expired) and compare code
        if (cacheCode == null || !cacheCode.equals(code)) {
            return Result.fail("Code is error");
        }

        //5. Delete code after successful validation(prevent reuse)
        stringRedisTemplate.delete(LOGIN_CODE_KEY + phone);

        //6. Query user by phone
        User user = userMapper.findByPhone(phone);

        //7. If user not exists -> create new user
        if (user == null) {
            user = createUser(phone);
        }

        //8. generate JWT

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setNickname(user.getNickname());
        String token = jwtUtils.generateToken(user.getId(), user.getNickname());
        response.setToken(token);

        return Result.ok(response);
    }

    private User createUser(String phone) {
        User user = new User();

        // Set phone number
        user.setPhone(phone);

        // Generate default nickname
        user.setNickname(USER_NICK_NAME_PREFIX + RandomUtils.randomString8());

        // Save user to database
        userMapper.insert(user);

        return user;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}


