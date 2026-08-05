package com.supermap.service;

import com.supermap.dao.GeometryDao;
import com.supermap.enums.GeomType;
import com.supermap.type.Column;
import com.supermap.type.TableProcessResult;
import com.supermap.util.TempTableNameGenerator;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
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

    public String transformTable(String schema,
                                 List<Column> columns,
                                 GeomType geomType,
                                 String sourceTable,
                                 int targetSrid) {
        String tempTableName = tempTableNameGenerator.getTableName();
        String postgisGeometryType = geomType.getPostgisGeometryTypeWithoutSt();
        List<String> columnNames = columns.stream().map(Column::name).toList();

        geometryDao.transformTable(schema, columnNames, postgisGeometryType, sourceTable, targetSrid, tempTableName);
        geometryDao.createGistIndex(schema, tempTableName);
        return tempTableName;
    }

    public TableProcessResult normalizeGeometry(String schema,
                                                String tableName,
                                                List<Column> columns,
                                                GeomType geomType,
                                                Integer srid) {
        String tempTableName = tempTableNameGenerator.getTableName();
        int i = geometryDao.countNeedNormalize(schema, tableName, geomType.getPostgisGeometryType());
        if (i == 0)
            return new TableProcessResult(tableName, false);

        List<String> columnNames = columns.stream().map(Column::name).toList();

        geometryDao.normalizeGeometry(schema,
                tableName,
                columnNames,
                tempTableName,
                geomType.getCollectionExtractType(),
                geomType.getPostgisGeometryTypeWithoutSt(),
                srid);
        geometryDao.createGistIndex(schema, tempTableName);

        return new TableProcessResult(tempTableName, true);
    }

    public void dropTableIfExists(String table) {
        geometryDao.dropTableIfExists(table);
    }

    public void createGistIndex(String schema, String table) {
        geometryDao.createGistIndex(schema, table);
    }

    /**
     * 重命名表（将临时表改名为结果表，标志分析流程完成）
     */
    public void renameTable(String current, String resultTableName) {
        geometryDao.renameTable(current, resultTableName);
    }

    /**
     * 统计表中的要素数量
     */
    public long getFeatureCount(String table) {
        return geometryDao.getFeatureCount(table);
    }

    /**
     * 为结果表添加自增主键（id 列需已通过 row_number() 生成）
     */
    public void addPrimaryKey(String schema, String table, String pkCol) {
        geometryDao.alterIdNotNull(schema, table, pkCol);
        geometryDao.addPrimaryKey(schema, table, pkCol);
    }

    public List<Column> listAttrColumns(String schema, String tableName, String pkCol) {
        return geometryDao.listAttrColumns(schema, tableName, pkCol);
    }

    /**
     * 从 PostgreSQL 目标表读取实际几何类型；读取失败或表为空时回退到源文件声明的类型
     */
    public GeomType resolveActualGeomType(String tableName, String fallbackGeomType) {
        String pgType = geometryDao.getGeometryType(tableName);
        GeomType geomType = GeomType.ofPostgisCode(pgType);
        if (geomType != null) {
            return geomType;
        }
        return GeomType.ofOgr2ogrCode(fallbackGeomType);
    }

    public void copyTable(String tableName,
                          String newTableName,
                          String schema,
                          List<Column> columns,
                          Integer originSrid,
                          GeomType geomType,
                          Integer srid) {
        String postgisGeometryType = geomType.getPostgisGeometryTypeWithoutSt();

        List<String> columnNames = columns.stream().map(Column::name).toList();
        geometryDao.copyTable(tableName, newTableName, schema, columnNames, originSrid, postgisGeometryType, srid);
    }

}
