package com.supermap.service;

import com.supermap.dao.GeometryDao;
import com.supermap.enums.GeomType;
import com.supermap.type.Column;
import com.supermap.type.TableProcessResult;
import com.supermap.util.TempTableNameGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gzw
 */
@Service
@AllArgsConstructor
public class GeometryService {

    private final GeometryDao geometryDao;

    private final TempTableNameGenerator tempTableNameGenerator;

    public String transformTable(String schema, String sourceTable, int targetSrid) {
        String tempTableName = tempTableNameGenerator.getTableName();
        geometryDao.transformTable(schema, sourceTable, targetSrid, tempTableName);
        geometryDao.createGistIndex(schema, tempTableName);
        return tempTableName;
    }

    public TableProcessResult normalizeGeometry(String schema,
                                                String tableName,
                                                List<Column> columns,
                                                GeomType geomType) {
        String tempTableName = tempTableNameGenerator.getTableName();
        int i = geometryDao.countNeedNormalize(schema, tableName, geomType.getPostgisCode());
        if (i == 0)
            return new TableProcessResult(tableName, false);

        List<String> columnNames = columns.stream().map(Column::name).toList();
        geometryDao.normalizeGeometry(schema, tableName, columnNames, tempTableName, geomType.getDimension());
        geometryDao.createGistIndex(schema, tempTableName);

        return new TableProcessResult(tempTableName, true);
    }

    public void dropTableIfExists(String table) {
        geometryDao.dropTableIfExists(table);
    }

    public List<Column> listAttrColumns(String schema, String tableName) {
        return geometryDao.listAttrColumns(schema, tableName);
    }

    /**
     * 查询表的实际几何类型，返回 ogr2ogr 可用的类型名称（如 MULTIPOLYGON）
     */
    public String getOgr2ogrGeometryType(String table) {
        String pgType = geometryDao.getGeometryType(table);
        if (pgType == null) {
            return "GEOMETRY";
        }
        // ST_GeometryType 返回形如 "ST_MultiPolygon" 的字符串，
        // 去掉 "ST_" 前缀并转大写即可得到 ogr2ogr 的 -nlt 参数值
        return pgType.startsWith("ST_") ? pgType.substring(3).toUpperCase() : pgType.toUpperCase();
    }

}
