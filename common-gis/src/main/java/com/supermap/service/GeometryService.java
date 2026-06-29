package com.supermap.service;

import com.supermap.dao.GeometryDao;
import com.supermap.enums.GeomType;
import com.supermap.type.Column;
import com.supermap.type.TableProcessResult;
import com.supermap.util.DsAnalyzeTempSnGenerator;
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

    private final DsAnalyzeTempSnGenerator dsAnalyzeTempSnGenerator;

    public String transformTable(String sourceTable, int targetSrid) {
        String tempTableName = dsAnalyzeTempSnGenerator.getTempTableName();
        geometryDao.transformTable(sourceTable, targetSrid, tempTableName);
        geometryDao.createGistIndex(tempTableName);
        return tempTableName;
    }

    public TableProcessResult normalizeGeometry(String tableName, List<Column> columns, GeomType geomType) {
        String tempTableName = dsAnalyzeTempSnGenerator.getTempTableName();
        int i = geometryDao.countNeedNormalize(tableName, geomType.getPostgisCode());
        if (i == 0)
            return new TableProcessResult(tableName, false);

        geometryDao.normalizeGeometry(tableName, columns, tempTableName, geomType.getDimension());
        geometryDao.createGistIndex(tempTableName);

        return new TableProcessResult(tempTableName, true);
    }

    public void dropTableIfExists(String table) {
        geometryDao.dropTableIfExists(table);
    }

    public List<Column> listAttrColumns(String tableName) {
        return geometryDao.listAttrColumns(tableName);
    }

}
