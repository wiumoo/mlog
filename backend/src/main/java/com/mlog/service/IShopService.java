package com.mlog.service;

import com.mlog.dto.Result;

public interface IShopService {
    // Get active shop list
    Result list();

    // Get shop detail by shop id
    Result detail(Long id);
}
