package com.mlog.service.impl;

import ch.qos.logback.core.encoder.JsonEscapeUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlog.dto.Result;
import com.mlog.entity.Shop;
import com.mlog.mapper.ShopMapper;
import com.mlog.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.mlog.utils.RedisConstants.*;
import static com.mlog.utils.RedisConstants.LOCK_SHOP_DETAIL_KEY;
import static com.mlog.utils.RedisConstants.LOCK_SHOP_DETAIL_TTL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements IShopService {

    private final ShopMapper shopMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public Result list() {
        // Query shop list from MySQL first.
        // Redis cache will be added after the basic API works.
        List<Shop> shops = shopMapper.findAll();
        return Result.ok(shops);
    }

    @Override
    public Result detail(Long id) {
        String key = CACHE_SHOP_DETAIL_KEY + id;

        // 1. Query shop detail from Redis first
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        // 2. Cache hit: return cached shop data
        if (shopJson != null && !shopJson.isEmpty()) {
            log.info("[SHOP_DETAIL_CACHE_HIT] key={}", key);

            try {
                Shop shop = objectMapper.readValue(shopJson, Shop.class);
                return Result.ok(shop);
            } catch (JsonProcessingException e) {
                log.error("[SHOP_DETAIL_CACHE_PARSE_ERROR] key={}", key, e);

                // Delete broken cache data
                stringRedisTemplate.delete(key);
            }
        }

        // 3. NULL cache hit: shop does not exist
        if (shopJson != null) {
            log.info("[SHOP_DETIAL_BULL_CACHE_HIT] key={}", key);
            return Result.fail("Shop not found");
        }
        log.info("[SHOP_DETAIL_CACHE_MISS] key={}", key);

        // 4. Try to acquire Redis mutex lock
        String lockKey = LOCK_SHOP_DETAIL_KEY + id;
        Boolean lock = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_SHOP_DETAIL_TTL, TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(lock)) {
            log.info("[SHOP_DETAIL_LOCK_FAILED] key={}", lockKey);

            try {
                // Wait briefly and retry Redis cache
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Result.fail("Request interrupted");
            }

            String retryJson = stringRedisTemplate.opsForValue().get(key);

            if (retryJson != null && !retryJson.isEmpty()) {
                log.info("[SHOP_DETAIL_CACHE_HIT_AFTER_WAIT] key={}", key);

                try {
                    Shop retryShop = objectMapper.readValue(retryJson, Shop.class);
                    return Result.ok(retryShop);
                } catch (JsonProcessingException e) {
                    log.error("[SHOP_DETAIL_CACHE_PARSE_ERROR] key={}", key, e);
                    stringRedisTemplate.delete(key);
                }
            }

            if (retryJson != null) {
                log.info("[SHOP_DETAIL_NULL_CACHE_HIT_AFTER_WAIT] key={}", key);
                return Result.fail("Shop not found");
            }

            return Result.fail("Please try again later");
        }

        try {
            log.info("[SHOP_DETAIL_LOCK_ACQUIRED] key={}", lockKey);

            // 5. Double check Redis after acquiring lock
            String doubleCheckJson = stringRedisTemplate.opsForValue().get(key);

            if (doubleCheckJson != null && !doubleCheckJson.isEmpty()) {
                log.info("[SHOP_DETAIL_CACHE_HIT_AFTER_LOCK] key={}", key);

                try {
                    Shop cachedShop = objectMapper.readValue(doubleCheckJson, Shop.class);
                    return Result.ok(cachedShop);
                } catch (JsonProcessingException e) {
                    log.error("[SHOP_DETAIL_CACHE_PARSE_ERROR] key={}", key, e);
                    stringRedisTemplate.delete(key);
                }
            }

            if (doubleCheckJson != null) {
                log.info("[SHOP_DETAIL_NULL_CACHE_HIT_AFTER_LOCK] key={}", key);
                return Result.fail("Shop not found");
            }

            // 6. Cache miss: query MySQL
            Shop shop = shopMapper.findById(id);

            // 7. If shop does not exist, cache empty value to prevent cache penetration
            if (shop == null) {
                stringRedisTemplate.opsForValue()
                        .set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);

                log.info("[SHOP_DETAIL_NULL_CACHE_SAVE] key={}", key);
                return Result.fail("Shop not found");
            }

            // 8. Save shop data to Redis with random TTL
            try {
                String json = objectMapper.writeValueAsString(shop);

                long ttl = CACHE_SHOP_DETAIL_TTL + new Random().nextInt(10);

                stringRedisTemplate.opsForValue()
                        .set(key, json, ttl, TimeUnit.MINUTES);

                log.info("[SHOP_DETAIL_CACHE_SAVE] key={}, ttl={}min", key, ttl);
            } catch (JsonProcessingException e) {
                log.error("[SHOP_DETAIL_CACHE_SAVE_ERROR] key={}", key, e);
            }

            return Result.ok(shop);

        } finally {
            // 9. Release Redis mutex lock
            stringRedisTemplate.delete(lockKey);
            log.info("[SHOP_DETAIL_LOCK_RELEASED] key={}", lockKey);
        }
    }
}
