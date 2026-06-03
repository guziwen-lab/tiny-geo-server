package com.supermap.analyze.security;

import org.springframework.util.Assert;

/**
 * @author gzw
 */
public class SqlInjectionCheck {

    public static boolean check(String sql) {
        if (sql == null || sql.isEmpty()) {
            return false;
        }
        String[] keywords = {"select", "insert", "update", "delete", "drop", "alter", "create", "union"};
        for (String keyword : keywords) {
            if (sql.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public static void checkTableName(String... tableName) {
        for (String name : tableName) {
            Assert.isTrue(name.matches("^ds_[A-Za-z0-9_]+$"), "Invalid table name: " + name);
        }
    }

}
