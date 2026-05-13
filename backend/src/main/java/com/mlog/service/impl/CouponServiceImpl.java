package com.mlog.service.impl;

import com.mlog.dto.CouponClaimMessage;
import com.mlog.dto.Result;
import com.mlog.entity.CouponEvent;
import com.mlog.entity.UserCoupon;
import com.mlog.mapper.CouponEventMapper;
import com.mlog.mapper.UserCouponMapper;
import com.mlog.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static com.mlog.utils.KafkaConstants.COUPON_CLAIM_TOPIC;
import static com.mlog.utils.RedisConstants.COUPON_STOCK_KEY;
import static com.mlog.utils.RedisConstants.COUPON_USER_KEY;
import static com.mlog.utils.RedisConstants.COUPON_EVENT_KEY;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {

    private final CouponEventMapper couponEventMapper;
    private final UserCouponMapper userCouponMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String, CouponClaimMessage> kafkaTemplate;

    private static final DefaultRedisScript<Long> COUPON_CLAIM_SCRIPT;
    private static final DefaultRedisScript<Long> COUPON_ROLLBACK_SCRIPT;

    static {
        COUPON_CLAIM_SCRIPT = new DefaultRedisScript<>();
        COUPON_CLAIM_SCRIPT.setLocation(new ClassPathResource("lua/coupon_claim.lua"));
        COUPON_CLAIM_SCRIPT.setResultType(Long.class);

        COUPON_ROLLBACK_SCRIPT = new DefaultRedisScript<>();
        COUPON_ROLLBACK_SCRIPT.setLocation(new ClassPathResource("lua/coupon_rollback.lua"));
        COUPON_ROLLBACK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result getEvent(Long eventId) {
        log.info("coupon getEvent eventId={}", eventId);

        CouponEvent event = couponEventMapper.findById(eventId);

        log.info("coupon getEvent result={}", event);

        if (event == null) {
            return Result.fail("쿠폰 이벤트가 존재하지 않습니다.");
        }

        return Result.ok(event);
    }

    @Override
    public Result saveUserCoupon(Long eventId, Long userId) {
        try {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setEventId(eventId);
            userCoupon.setUserId(userId);
            userCoupon.setStatus(1);

            userCouponMapper.insert(userCoupon);

            log.info("user coupon saved. eventId={}, userId={}", eventId, userId);

            return Result.ok();

        } catch (DuplicateKeyException e) {
            log.warn("duplicate coupon claim. eventId={}, userId={}", eventId, userId);
            return Result.fail("이미 쿠폰을 받았습니다.");
        }
    }

    @Override
    public Result loadCouponStock(Long eventId) {
        CouponEvent event = couponEventMapper.findById(eventId);

        if (event == null) {
            return Result.fail("쿠폰 이벤트가 존재하지 않습니다.");
        }

        String stockKey = COUPON_STOCK_KEY + eventId;
        String userKey = COUPON_USER_KEY + eventId;
        String eventKey = COUPON_EVENT_KEY + eventId;

        long startTime = event.getStartTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long endTime = event.getEndTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Map<String, String> eventMeta = new HashMap<>();
        eventMeta.put("startTime", String.valueOf(startTime));
        eventMeta.put("endTime", String.valueOf(endTime));
        eventMeta.put("status", String.valueOf(event.getStatus()));

        // Redis에 쿠폰 재고 적재
        stringRedisTemplate.opsForValue()
                .set(stockKey, String.valueOf(event.getTotalStock()));

        // Redis에 이벤트 시간/status 정보 적재
        stringRedisTemplate.opsForHash()
                .putAll(eventKey, eventMeta);

        // 테스트 편의를 위해 기존 발급 유저 Set 초기화
        // 운영 환경에서는 신중하게 처리해야 함
        stringRedisTemplate.delete(userKey);

        log.info("coupon event loaded. eventId={}, stock={}, startTime={}, endTime={}, status={}",
                eventId,
                event.getTotalStock(),
                startTime,
                endTime,
                event.getStatus());

        return Result.ok();
    }

    @Override
    public Result claimCoupon(Long eventId, Long userId) {
        String stockKey = COUPON_STOCK_KEY + eventId;
        String userKey = COUPON_USER_KEY + eventId;

        String eventKey = COUPON_EVENT_KEY + eventId;
        String now = String.valueOf(System.currentTimeMillis());

        Long result = stringRedisTemplate.execute(
                COUPON_CLAIM_SCRIPT,
                Arrays.asList(stockKey, userKey, eventKey),
                userId.toString(),
                now
        );

        if (result == null) {
            return Result.fail("쿠폰 발급 처리 중 오류가 발생했습니다.");
        }

        int code = result.intValue();

        if (code == 1) {
            return Result.fail("쿠폰이 모두 소진되었습니다.");
        }

        if (code == 2) {
            return Result.fail("이미 쿠폰을 받았습니다.");
        }

        if (code == 3) {
            return Result.fail("쿠폰 재고가 아직 Redis에 적재되지 않았습니다.");
        }

        if (code == 4) {
            return Result.fail("쿠폰 이벤트 정보가 아직 Redis에 적재되지 않았습니다.");
        }

        if (code == 5) {
            return Result.fail("비활성화된 쿠폰 이벤트입니다.");
        }

        if (code == 6) {
            return Result.fail("아직 쿠폰 이벤트가 시작되지 않았습니다.");
        }

        if (code == 7) {
            return Result.fail("쿠폰 이벤트가 종료되었습니다.");
        }

        CouponClaimMessage message = new CouponClaimMessage();
        message.setEventId(eventId);
        message.setUserId(userId);
        message.setRequestId(UUID.randomUUID().toString());
        message.setCreatedAt(LocalDateTime.now());

        kafkaTemplate.send(
                COUPON_CLAIM_TOPIC,
                eventId + ":" + userId,
                message
        ).whenComplete((sendResult, ex) -> {
            if (ex != null) {
                log.error("coupon claim kafka send failed. eventId={}, userId={}, requestId={}",
                        eventId, userId, message.getRequestId(), ex);

                rollbackCouponClaim(eventId, userId);
                return;
            }

            log.info("coupon claim kafka message sent. eventId={}, userId={}, requestId={}, offset={}",
                    eventId,
                    userId,
                    message.getRequestId(),
                    sendResult.getRecordMetadata().offset());
        });

        log.info("coupon claim accepted. eventId={}, userId={}", eventId, userId);

        return Result.ok();
    }

    private void rollbackCouponClaim(Long eventId, Long userId) {
        String stockKey = COUPON_STOCK_KEY + eventId;
        String userKey = COUPON_USER_KEY + eventId;

        Long rollbackResult = stringRedisTemplate.execute(
                COUPON_ROLLBACK_SCRIPT,
                Arrays.asList(stockKey, userKey),
                userId.toString()
        );

        log.warn("coupon claim rollback executed. eventId={}, userId={}, result={}",
                eventId, userId, rollbackResult);
    }
}