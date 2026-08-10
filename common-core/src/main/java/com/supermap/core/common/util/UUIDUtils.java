package com.supermap.core.common.util;

import java.util.UUID;

/**
 * @author gzw
 */
public class UUIDUtils {

    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
