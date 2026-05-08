package com.mlog.scheduler;

import com.mlog.mapper.BlogVisitStatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static com.mlog.utils.RedisConstants.BLOG_VISIT_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogVisitStatsScheduler {

    private final StringRedisTemplate stringRedisTemplate;
    private final BlogVisitStatsMapper blogVisitStatsMapper;

    /**
     * 매일 00:05에 어제 날짜의 블로그 방문자 수를 MySQL에 저장한다.
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void saveYesterdayBlogVisitStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        saveBlogVisitStatsByDate(yesterday);
    }

    /**
     * 테스트와 재사용을 위해 날짜별 저장 로직을 분리한다.
     */
    public void saveBlogVisitStatsByDate(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String pattern = BLOG_VISIT_KEY + "*:" + dateStr;

        Set<String> keys = stringRedisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("No blog visit stats keys found. date={}", date);
            return;
        }

        for (String key : keys) {
            Long blogId = parseBlogIdFromKey(key);
            if (blogId == null) {
                log.warn("Invalid blog visit key format. key={}", key);
                continue;
            }

            Long uv = stringRedisTemplate.opsForHyperLogLog().size(key);
            if (uv == null) {
                uv = 0L;
            }

            blogVisitStatsMapper.upsertDailyStats(blogId, date, uv);

            log.info("Saved blog visit stats. blogId={}, date={}, uv={}", blogId, date, uv);
        }
    }

    private Long parseBlogIdFromKey(String key) {
        try {
            // key format: blog:visit:{blogId}:{yyyyMMdd}
            String[] parts = key.split(":");
            return Long.valueOf(parts[2]);
        } catch (Exception e) {
            return null;
        }
    }
}