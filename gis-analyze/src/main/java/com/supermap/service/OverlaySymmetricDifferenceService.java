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
public class OverlaySymmetricDifferenceService extends AbstractOverlayExecuteService {

    @Override
    public String geometryExpression(GeomType geomType, int srid) {
        // FULL OUTER JOIN 两侧均可能产生 NULL：
        // - a.geom 为 NULL → B-only 要素，返回 b.geom
        // - b.geom 为 NULL → A-only 要素，返回 a.geom
        // - 两者都不为 NULL → 计算对称差
        return GeometryExpression.wrap(
                "CASE WHEN a.geom IS NULL THEN b.geom WHEN b.geom IS NULL THEN a.geom ELSE ST_SymDifference(a.geom, b.geom) END",
                geomType, srid);
    }

    @Override
    public String buildSql(String current, String next, String result, String selectClause) {
        // 先将 B 的所有几何 Union 成一个整体，避免多对多 JOIN 产生部分对称差；
        // FULL OUTER JOIN 同时保留 A-only 和 B-only 的不相交要素
        return """
                CREATE TABLE %s AS
                SELECT
                %s
                FROM %s a
                FULL OUTER JOIN (
                  SELECT ST_Union(geom) AS geom FROM %s
                ) b ON ST_Intersects(a.geom, b.geom)
                """.formatted(
                result,
                selectClause,
                current,
                next);
    }

    @Override
    protected String buildSelectClause(List<Column> currentColumns, List<Column> nextColumns, String geometryExpression) {
        // 对称差只保留输入图层属性
        return super.buildSelectClause(currentColumns, Collections.emptyList(), geometryExpression);
    }

}
