package com.supermap.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Multi Polygon
 * Point
 * Multi Line String
 *
 * @author gzw
 */
@AllArgsConstructor
@Getter
public enum GeomType {

    POINT("Point", 0),
    MULTI_LINE_STRING("Multi Line String", 1),
    MULTI_POLYGON("Multi Polygon", 2);

    private final String code;

    private final int dimension;

    public static GeomType of(String code) {
        for (GeomType geomType : GeomType.values()) {
            if (geomType.getCode().equals(code)) {
                return geomType;
            }
        }
        return null;
    }

}
