package com.supermap.analyze.service.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.service.AbstractExecuteService;
import com.supermap.analyze.task.param.RepairGeometryParam;
import com.supermap.gis.enums.GeomType;
import com.supermap.gis.util.TableNameUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gzw
 */
@Service
public class RepairGeometryExecuteService extends AbstractExecuteService<RepairGeometryParam> {

    @Override
    protected String buildExecuteSql(LayerInfo current,
                                     LayerInfo next,
                                     String resultTableName,
                                     AnalysisContext<RepairGeometryParam> context) {
        List<String> selectItems = createSingleTableSelectItems(current, context);

        RepairGeometryParam param = context.getParam();
        if (param != null && param.getSrid() != null) {
            Integer srid = param.getSrid();
            GeomType geomType = context.getGeomType();
            String postgisGeometryType = geomType.getPostgisGeometryTypeWithoutSt();
            selectItems.add("ST_Transform(geom, %d)::geometry(%s, %d) AS geom".formatted(srid, postgisGeometryType, srid));
        } else {
            selectItems.add("geom::geometry(${postgisGeometryType}, ${originSrid}) AS geom");
        }

        String schema = context.getSchema();
        String tableName = current.getTableName();
        String inputTable = TableNameUtils.getTableNameWithSchema(schema, tableName);
        String newTable = TableNameUtils.getTableNameWithSchema(schema, resultTableName);

        return """
                CREATE TABLE %s AS
                SELECT
                 %s
                FROM %s
                """.formatted(newTable,
                String.join(",\n", selectItems),
                inputTable);
    }

}
