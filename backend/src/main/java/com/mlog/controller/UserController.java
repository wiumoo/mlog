package com.mlog.controller;

import com.mlog.dto.LoginRequest;
import com.mlog.dto.Result;
import com.mlog.entity.User;
import com.mlog.service.IUserService;
import com.mlog.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone) {
        return userService.sendCode(phone);
    }

    @PostMapping("login")
    public Result login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public Result me() {

        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        User user = userService.getById(userId);

        return Result.ok(user);
    }
}
