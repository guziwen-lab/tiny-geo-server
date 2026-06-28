package com.supermap.service;

import com.supermap.enums.GeomType;
import com.supermap.type.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlayClipService extends AbstractOverlayExecuteService {

    @Override
    public String geometryExpression(GeomType geomType) {
        return GeometryExpression.wrap("ST_Intersection(a.geom,b.geom)", geomType);
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

    @Override
    protected String buildSelectClause(List<Column> currentColumns, List<Column> nextColumns, String geometryExpression) {
        return super.buildSelectClause(currentColumns, Collections.emptyList(), geometryExpression);
    }

}
