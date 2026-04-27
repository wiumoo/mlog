package com.mlog.service;

import com.mlog.dto.LoginRequest;
import com.mlog.dto.Result;
import com.mlog.entity.User;

public interface IUserService {

    Result sendCode(String phone);

    Result login(LoginRequest request);

    User getById(Long id);
}
