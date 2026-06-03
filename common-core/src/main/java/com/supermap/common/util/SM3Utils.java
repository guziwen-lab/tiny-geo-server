package com.supermap.common.util;

import cn.hutool.crypto.SmUtil;

/**
 * @author gzw
 */
public class SM3Utils {

    public static String digest(String str) {
        return SmUtil.sm3(str);
    }

}
