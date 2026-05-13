package com.mlog.controller;

import com.mlog.dto.Result;
import com.mlog.service.ICouponService;
import com.mlog.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final ICouponService couponService;

    @GetMapping("/events/{eventId}")
    public Result getEvent(@PathVariable Long eventId) {
        return couponService.getEvent(eventId);
    }

    /**
     * 쿠폰 이벤트 재고를 Redis에 적재하는 API
     * 현재는 개발/테스트용
     */
    @PostMapping("/events/{eventId}/load")
    public Result loadCouponStock(@PathVariable Long eventId) {
        return couponService.loadCouponStock(eventId);
    }

    /**
     * 임시 테스트용 API
     * 나중에는 Kafka Consumer가 DB 저장을 담당하게 할 예정
     */
    @PostMapping("/events/{eventId}/test-save")
    public Result testSaveUserCoupon(@PathVariable Long eventId) {
        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        return couponService.saveUserCoupon(eventId, userId);
    }

    @PostMapping("/events/{eventId}/claim")
    public Result claimCoupon(@PathVariable Long eventId) {
        Long userId = UserHolder.getUser();

        if (userId == null) {
            return Result.fail("로그인이 필요합니다.");
        }

        return couponService.claimCoupon(eventId, userId);
    }


}