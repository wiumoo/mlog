package com.mlog.consumer;

import com.mlog.dto.CouponClaimMessage;
import com.mlog.dto.Result;
import com.mlog.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.mlog.utils.KafkaConstants.COUPON_CLAIM_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponClaimConsumer {

    private final ICouponService couponService;

    @KafkaListener(
            topics = COUPON_CLAIM_TOPIC,
            groupId = "mlog-coupon-group",
            containerFactory = "couponClaimKafkaListenerContainerFactory"
    )
    public void consume(CouponClaimMessage message) {
        log.info("coupon claim message received. message={}", message);

        Result result = couponService.saveUserCoupon(
                message.getEventId(),
                message.getUserId()
        );

        if (Boolean.TRUE.equals(result.getSuccess())) {
            log.info("coupon claim saved to DB. eventId={}, userId={}, requestId={}",
                    message.getEventId(),
                    message.getUserId(),
                    message.getRequestId());
            return;
        }

        log.warn("coupon claim save skipped or failed. eventId={}, userId={}, requestId={}, errorMsg={}",
                message.getEventId(),
                message.getUserId(),
                message.getRequestId(),
                result.getErrorMsg());
    }
}