package com.supermap.security;

import org.springframework.util.Assert;

/**
 * @author gzw
 */
public class SqlInjectionCheck {

    public static void checkTableName(String... tableName) {
        for (String name : tableName) {
            Assert.isTrue(name.matches("^ds_[A-Za-z0-9_]+$"), "Invalid table name: " + name);
        }
    }

}
