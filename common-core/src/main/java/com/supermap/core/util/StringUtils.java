package com.supermap.core.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author gzw
 */
public class StringUtils extends StrUtil {

    public static String numberToStringNum(String value) {
        if (isEmpty(value)) {
            return "0";
        }
        return value;
    }

    public static String limit(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength
                ? value.substring(0, maxLength) + "..."
                : value;
    }

}
