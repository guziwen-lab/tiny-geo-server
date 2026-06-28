package com.supermap.enums;

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

    POINT("Point", "POINT", 1),
    MULTI_POINT("Multi Point", "MULTIPOINT", 1),
    LINE_STRING("Line String", "LINESTRING", 2),
    MULTI_LINE_STRING("Multi Line String", "MULTILINESTRING", 2),
    POLYGON("Polygon", "POLYGON", 3),
    MULTI_POLYGON("Multi Polygon", "MULTIPOLYGON", 3);

    private final String ogr2ogrCode;

    private final String postgisCode;

    private final int dimension;

    public static GeomType of(String code) {
        for (GeomType geomType : GeomType.values()) {
            if (geomType.getOgr2ogrCode().equals(code)) {
                return geomType;
            }
        }
        return null;
    }

    public boolean isPoint() {
        return dimension == 0;
    }

    public boolean isLine() {
        return dimension == 1;
    }

    public boolean isPolygon() {
        return dimension == 2;
    }

    public int collectionExtractType() {
        return switch (dimension) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            default -> throw new IllegalStateException();
        };
    }

}
