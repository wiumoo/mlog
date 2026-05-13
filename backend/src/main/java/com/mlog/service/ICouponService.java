package com.mlog.service;

import com.mlog.dto.Result;

public interface ICouponService {

    Result getEvent(Long eventId);

    Result saveUserCoupon(Long eventId, Long userId);

    Result loadCouponStock(Long eventId);

    Result claimCoupon(Long eventId, Long userId);


}