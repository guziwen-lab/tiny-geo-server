package com.supermap.service.impl;

import com.supermap.enums.GeomType;
import com.supermap.enums.OverlayAlgorithm;
import com.supermap.service.AbstractOverlayExecuteService;
import com.supermap.service.GeometryExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlayIntersectService extends AbstractOverlayExecuteService {

    @Override
    public OverlayAlgorithm getAlgorithm() {
        return OverlayAlgorithm.INTERSECT;
    }

    @Override
    public String geometryExpression(GeomType geomType, int srid) {
        return GeometryExpression.wrap("ST_Intersection(a.geom, b.geom)", geomType, srid);
    }

    @Override
    public String buildSql(String current, String next, String result, String selectClause) {
        return """
                CREATE TABLE %s AS
                SELECT
                %s
                FROM %s a
                JOIN %s b
                  ON ST_Intersects(a.geom, b.geom)
                """.formatted(
                result,
                selectClause,
                current,
                next);
    }

}
