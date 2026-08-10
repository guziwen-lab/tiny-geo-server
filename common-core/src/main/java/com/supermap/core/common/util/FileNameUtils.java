package com.supermap.core.common.util;

import cn.hutool.core.io.file.FileNameUtil;

/**
 * @author gzw
 */
public class FileNameUtils extends FileNameUtil {

    public static String getFileNameWithoutExtension(String path) {
        String name = path;
        int sep = name.lastIndexOf('/');
        if (sep < 0) {
            sep = name.lastIndexOf('\\');
        }
        if (sep >= 0) {
            name = name.substring(sep + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(0, dot);
        }
        return name;
    }

}
