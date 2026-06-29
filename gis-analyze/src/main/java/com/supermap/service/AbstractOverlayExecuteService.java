package com.supermap.service;

import com.supermap.AnalysisContext;
import com.supermap.LayerInfo;
import com.supermap.enums.GeomType;
import com.supermap.task.OverlayParam;
import com.supermap.type.Column;

import java.util.*;

/**
 * @author gzw
 */
public abstract class AbstractOverlayExecuteService extends AbstractExecuteService<OverlayParam> {

    @Override
    protected String buildExecuteSql(LayerInfo current, LayerInfo next, String result, AnalysisContext<OverlayParam> context) {
        List<Column> currentColumns = current.getColumns();
        List<Column> nextColumns = next.getColumns();
        String selectClause = buildSelectClause(currentColumns, nextColumns, geometryExpression(context.getGeomType()));

        return buildSql(current.getTableName(), next.getTableName(), result, selectClause);
    }

    abstract String geometryExpression(GeomType geomType);

    abstract String buildSql(String current, String next, String result, String selectClause);

    protected String buildSelectClause(List<Column> currentColumns,
                                       List<Column> nextColumns,
                                       String geometryExpression) {
        Set<String> usedNames = new HashSet<>();
        List<String> selectItems = new ArrayList<>();

        // 当前图层字段
        for (Column column : currentColumns) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            selectItems.add(
                    "a.\"%s\" AS \"%s\""
                            .formatted(column.name(), alias)
            );
        }

        // 叠加图层字段
        for (Column column : nextColumns) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            selectItems.add(
                    "b.\"%s\" AS \"%s\""
                            .formatted(column.name(), alias)
            );
        }

        // Geometry
        selectItems.add(geometryExpression + " AS geom");

        return String.join(",\n", selectItems);
    }

}
