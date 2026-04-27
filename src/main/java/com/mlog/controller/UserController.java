package com.mlog.controller;

import com.mlog.dto.LoginRequest;
import com.mlog.dto.Result;
import com.mlog.service.IUserService;
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

}
