package com.supermap.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryParserUtils {

    private static final WKTReader WKT_READER = new WKTReader();

    public static Geometry parseWKT(String wkt) {
        try {
            return WKT_READER.read(wkt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法WKT", e);
        }
    }

}