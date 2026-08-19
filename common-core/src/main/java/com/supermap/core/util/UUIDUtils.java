package com.supermap.core.util;

import java.util.UUID;

/**
 * @author gzw
 */
public class UUIDUtils {

    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
