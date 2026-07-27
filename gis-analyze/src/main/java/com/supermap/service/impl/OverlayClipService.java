package com.supermap.service.impl;

import com.supermap.enums.GeomType;
import com.supermap.enums.OverlayAlgorithm;
import com.supermap.service.AbstractOverlayExecuteService;
import com.supermap.service.GeometryExpression;
import com.supermap.type.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Intersect 保留两侧属性 + 取交集，Clip 只保留 A 侧属性 + 取交集
 *
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlayClipService extends AbstractOverlayExecuteService {

    @Override
    public OverlayAlgorithm getAlgorithm() {
        return OverlayAlgorithm.CLIP;
    }

    @Override
    public String geometryExpression(GeomType geomType, int srid) {
        return GeometryExpression.wrap("ST_Intersection(a.geom,b.geom)", geomType, srid);
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

    @Override
    protected String buildSelectClause(List<Column> currentColumns, List<Column> nextColumns, String geometryExpression) {
        return super.buildSelectClause(currentColumns, Collections.emptyList(), geometryExpression);
    }

}
