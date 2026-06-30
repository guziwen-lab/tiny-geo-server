package com.supermap.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author gzw
 */
@RequiredArgsConstructor
public class AbstractSnGenerator {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String SN_PREFIX;

    public String generate() {
        LocalDate today = LocalDate.now();
        String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String redisKey = SN_PREFIX + datePrefix;

        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(redisKey);
        Long sequence = ops.increment();
        if (sequence == null)
            throw new RuntimeException("Failed to generate sequence for key: " + redisKey);
        if (sequence == 1L)
            ops.expire(2, TimeUnit.DAYS);

        // 格式化序号为6位，不足6位补零
        String sequenceStr = String.format("%06d", sequence);

        return datePrefix + sequenceStr;
    }

}
