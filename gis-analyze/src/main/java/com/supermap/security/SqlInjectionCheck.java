package com.supermap.security;

import org.springframework.util.Assert;

/**
 * @author gzw
 */
public class SqlInjectionCheck {

    private static final String TABLE_NAME_REGEX = "^[A-Za-z_][A-Za-z0-9_]+$";
    private static final String COLUMN_NAME_REGEX = "^[A-Za-z_][A-Za-z0-9_]*$";

    public static void checkTableName(String... tableName) {
        for (String name : tableName) {
            Assert.isTrue(name.matches(TABLE_NAME_REGEX), "Invalid table name: " + name);
        }
    }

    public static void checkColumnName(String... columnName) {
        for (String name : columnName) {
            Assert.isTrue(name.matches(COLUMN_NAME_REGEX), "Invalid column name: " + name);
        }
    }

}
