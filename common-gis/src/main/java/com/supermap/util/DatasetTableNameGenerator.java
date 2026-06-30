package com.supermap.util;

import org.springframework.stereotype.Component;

/**
 * 数据集表名生成器
 *
 * @author gzw
 */
@Component
public class DatasetTableNameGenerator extends AbstractSnGenerator {

    public DatasetTableNameGenerator() {
        super("DS:TABLE:");
    }

    public String getTableName() {
        return "dataset.ds_" + super.generate();
    }

}
