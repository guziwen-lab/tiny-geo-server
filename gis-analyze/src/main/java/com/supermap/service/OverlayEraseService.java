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
public class OverlayEraseService extends AbstractOverlayExecuteService {

    @Override
    public String geometryExpression(GeomType geomType, int srid) {
        // b.geom 为 NULL（A要素与B整体不相交）时，COALESCE 回退到原始几何，保证不相交要素不丢失
        return GeometryExpression.wrap("COALESCE(ST_Difference(a.geom, b.geom), a.geom)", geomType, srid);
    }

    @Override
    public String buildSql(String current, String next, String result, String selectClause) {
        // 先将 B 的所有几何 Union 成一个整体，避免多对多 JOIN 产生部分擦除；
        // LEFT JOIN 保留与 B 不相交的 A 要素
        return """
                CREATE TABLE %s AS
                SELECT
                %s
                FROM %s a
                LEFT JOIN (
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
        // Erase 只保留输入图层属性，叠加图层仅用于擦除
        return super.buildSelectClause(currentColumns, Collections.emptyList(), geometryExpression);
    }

}
