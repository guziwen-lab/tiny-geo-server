package com.supermap.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
