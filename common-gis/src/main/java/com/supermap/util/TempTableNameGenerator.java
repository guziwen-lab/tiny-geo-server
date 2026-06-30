package com.supermap.util;

import org.springframework.stereotype.Component;

/**
 * 订单id生成器
 *
 * @author gzw
 */
@Component
public class TempTableNameGenerator extends AbstractSnGenerator {

    public TempTableNameGenerator() {
        super("DS:TEMP:");
    }

    public String getTableName() {
        return "dataset.ds_temp_" + super.generate();
    }

}
