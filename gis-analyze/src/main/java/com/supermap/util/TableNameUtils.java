package com.supermap.util;

/**
 * @author gzw
 */
public class TableNameUtils {

    public static String getTableNameWithSchema(String schema, String tableName) {
        return schema + "." + tableName;
    }

}
