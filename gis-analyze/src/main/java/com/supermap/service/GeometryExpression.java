package com.supermap.service;

import com.supermap.enums.GeomType;

public final class GeometryExpression {

    private GeometryExpression() {
    }

    public static String wrap(String expr, GeomType geomType) {
        return switch (geomType) {
            case POINT, MULTI_POINT -> """
                    ST_Multi(
                        ST_CollectionExtract(
                            %s,
                            1
                        )
                    )
                    """.formatted(expr);
            case LINE_STRING, MULTI_LINE_STRING -> """
                    ST_Multi(
                        ST_CollectionExtract(
                            %s,
                            2
                        )
                    )
                    """.formatted(expr);
            case POLYGON, MULTI_POLYGON -> """
                    ST_Multi(
                        ST_CollectionExtract(
                            %s,
                            3
                        )
                    )
                    """.formatted(expr);
        };
    }

}