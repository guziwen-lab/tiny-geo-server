package com.supermap.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单id生成器
 *
 * @author gzw
 */
@Component
public class DsAnalyzeTempSnGenerator extends AbstractSnGenerator {

    public DsAnalyzeTempSnGenerator(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, "DS:ANALYZE:TEMP:SN:");
    }

    public String getTempTableName() {
        return "ds_analyze_temp_" + super.generate();
    }

}
