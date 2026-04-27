package com.mlog.service;

import com.mlog.dto.LoginRequest;
import com.mlog.dto.Result;

public interface IUserService {

    Result sendCode(String phone);

    Result login(LoginRequest request);
}
