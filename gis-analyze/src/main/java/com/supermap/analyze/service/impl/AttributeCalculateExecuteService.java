package com.supermap.analyze.service.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.service.AbstractExecuteService;
import com.supermap.analyze.task.param.AttributeCalculateParam;
import com.supermap.gis.type.Column;
import com.supermap.gis.util.TableNameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
public class AttributeCalculateExecuteService extends AbstractExecuteService<AttributeCalculateParam> {

    @Override
    protected String buildExecuteSql(LayerInfo current,
                                     LayerInfo next,
                                     String resultTableName,
                                     AnalysisContext<AttributeCalculateParam> context) {
        List<String> selectItems = getSelectItems(current, context);

        String tableName = current.getTableName();
        String schema = context.getSchema();
        String inputTable = TableNameUtils.getTableNameWithSchema(schema, tableName);
        String resultTable = TableNameUtils.getTableNameWithSchema(schema, resultTableName);

        return """
                CREATE TABLE %s AS
                SELECT
                %s
                FROM %s
                """.formatted(resultTable, String.join(",\n", selectItems), inputTable);
    }

    private static List<String> getSelectItems(LayerInfo current, AnalysisContext<AttributeCalculateParam> context) {
        List<AttributeCalculateParam.CalculatedField> fields = context.getParam().getFields();

        List<String> selectItems = new ArrayList<>();
        String pkCol = context.getPkCol();
        selectItems.add("row_number() OVER () AS " + pkCol);

        for (Column column : current.getColumns()) {
            selectItems.add("\"%s\"".formatted(column.name()));
        }
        for (AttributeCalculateParam.CalculatedField field : fields) {
            selectItems.add("(%s) AS \"%s\"".formatted(field.expression(), field.name()));
        }
        selectItems.add("geom");
        return selectItems;
    }

}
