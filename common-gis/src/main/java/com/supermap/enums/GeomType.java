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

    POINT("Point", "ST_Point", 1),
    MULTI_POINT("Multi Point", "ST_MultiPoint", 1),
    LINE_STRING("Line String", "ST_LineString", 2),
    MULTI_LINE_STRING("Multi Line String", "ST_MultiLineString", 2),
    POLYGON("Polygon", "ST_Polygon", 3),
    MULTI_POLYGON("Multi Polygon", "ST_MultiPolygon", 3);

    /**
     * OGR2OGR 几何类型
     */
    private final String geometryName;

    /**
     * PostGIS 几何类型
     */
    private final String postgisGeometryType;

    /**
     * ST_CollectionExtract 类型
     */
    private final int collectionExtractType;

    public static GeomType ofOgr2ogrCode(String code) {
        for (GeomType geomType : GeomType.values()) {
            if (geomType.getGeometryName().equals(code)) {
                return geomType;
            }
        }
        return null;
    }

    public static GeomType ofPostgisCode(String code) {
        for (GeomType geomType : GeomType.values()) {
            if (geomType.getPostgisGeometryType().equals(code)) {
                return geomType;
            }
        }
        return null;
    }

    public boolean isMulti() {
        return geometryName.startsWith("Multi");
    }

    public boolean isPoint() {
        return collectionExtractType == 0;
    }

    public boolean isLine() {
        return collectionExtractType == 1;
    }

    public boolean isPolygon() {
        return collectionExtractType == 2;
    }

    public int collectionExtractType() {
        return switch (collectionExtractType) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            default -> throw new IllegalStateException();
        };
    }

    /**
     * 返回 ogr2ogr -nlt 参数可识别的类型值，如 MULTIPOLYGON、LINESTRING 等
     */
    public String getOgr2ogrNltValue() {
        return getGeometryName().replace(" ", "").toUpperCase();
    }

    public String getPostgisGeometryTypeWithoutSt() {
        return getPostgisGeometryType().replace("ST_", "");
    }

}
