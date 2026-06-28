package com.supermap.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * shp, gdb
 *
 * @author gzw
 */
@Getter
@AllArgsConstructor
public enum DatasetType {

    SHP("shp"),
    GDB("gdb");

    private final String extension;

}
