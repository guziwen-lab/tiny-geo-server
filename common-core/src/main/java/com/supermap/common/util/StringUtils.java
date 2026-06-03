package com.supermap.common.util;

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
}
