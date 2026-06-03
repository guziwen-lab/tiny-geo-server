package com.supermap.util;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 订单id生成器
 *
 * @author gzw
 */
@Component
public class DsSnGenerator extends AbstractSnGenerator {

    public DsSnGenerator(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, "DS:SN:");
    }

}
