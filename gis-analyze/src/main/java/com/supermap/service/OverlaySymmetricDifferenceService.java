package com.supermap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlaySymmetricDifferenceService extends AbstractOverlayExecuteService {

    @Override
    public String geometryExpression() {
        return "ST_SymDifference(a.geom, b.geom)";
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
