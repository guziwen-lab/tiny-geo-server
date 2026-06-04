package com.supermap.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单id生成器
 *
 * @author gzw
 */
@Component
public class DsResultSnGenerator extends AbstractSnGenerator {

    public DsResultSnGenerator(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, "DS:RESULT:SN:");
    }

}
