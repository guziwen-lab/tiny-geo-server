package com.supermap.service;

import com.supermap.enums.GeomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlayIntersectService extends AbstractOverlayExecuteService {

    @Override
    public String geometryExpression(GeomType geomType) {
        return GeometryExpression.wrap("ST_Intersection(a.geom, b.geom)", geomType);
    }

    @Override
    public String buildSql(String current, String next, String result, String selectClause) {
        return """
                CREATE TABLE "%s" AS
                SELECT
                %s
                FROM "%s" a
                JOIN "%s" b
                  ON ST_Intersects(a.geom, b.geom)
                """.formatted(
                result,
                selectClause,
                current,
                next);
    }

}
