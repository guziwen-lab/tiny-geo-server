package com.supermap.analyze.service.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.service.AbstractExecuteService;
import com.supermap.analyze.task.param.FilterParam;
import com.supermap.gis.util.TableNameUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gzw
 */
@Service
public class FilterExecuteService extends AbstractExecuteService<FilterParam> {

    @Override
    protected String buildExecuteSql(LayerInfo current,
                                     LayerInfo next,
                                     String resultTableName,
                                     AnalysisContext<FilterParam> context) {
        List<String> selectItems = createSingleTableSelectItems(current, context);

        String schema = context.getSchema();
        String tableName = current.getTableName();
        String inputTable = TableNameUtils.getTableNameWithSchema(schema, tableName);
        String newTable = TableNameUtils.getTableNameWithSchema(schema, resultTableName);

        String whereClause = context.getParam().getWhereClause();

        return """
                CREATE TABLE %s AS
                SELECT
                 %s
                FROM %s
                WHERE %s
                """.formatted(newTable,
                String.join(",\n", selectItems),
                inputTable,
                whereClause);
    }

}
