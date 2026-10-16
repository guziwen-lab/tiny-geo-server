package com.supermap.gis.enums;

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

    /**
     * 将 ogrinfo 报告的图层几何类型归一化为支持的几何类型：
     * GDB 曲面图层常报告为 Multi Surface / Curve Polygon，无法直接落入基础类型，
     * 统一映射为同族 Multi 类型，由导入时的显式 -nlt 类型完成线性化转换
     */
    public static GeomType ofOgr2ogrCodeNormalize(String code) {
        if (code == null) {
            return null;
        }
        GeomType exact = ofOgr2ogrCode(code);
        if (exact != null) {
            return exact;
        }
        return switch (code) {
            case "Multi Surface", "Curve Polygon" -> MULTI_POLYGON;
            case "Multi Curve", "Compound Curve" -> MULTI_LINE_STRING;
            default -> null;
        };
    }

    /**
     * 提升为同族 Multi 类型，与 -nlt PROMOTE_TO_MULTI 的建列策略一致
     */
    public GeomType promoteToMulti() {
        return switch (this) {
            case POINT -> MULTI_POINT;
            case LINE_STRING -> MULTI_LINE_STRING;
            case POLYGON -> MULTI_POLYGON;
            default -> this;
        };
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
