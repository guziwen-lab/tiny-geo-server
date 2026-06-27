package com.supermap.service;

import com.supermap.dao.GeometryDao;
import com.supermap.util.DsAnalyzeTempSnGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        return tempTableName;
    }

    public String fixGeometryTypeByMultipolygon(String tableName) {
        String tempTableName = dsAnalyzeTempSnGenerator.getTempTableName();
        long l = geometryDao.fixGeometryTypeByMultipolygon(tableName);
        if (l <= 0) {
            return tableName;
        }
        return tempTableName;
    }

    public String fixGeometryTypeByMultiLineString(String tableName) {
        String tempTableName = dsAnalyzeTempSnGenerator.getTempTableName();
        long l = geometryDao.fixGeometryTypeByMultiLineString(tableName);
        if (l <= 0) {
            return tableName;
        }
        return tempTableName;
    }

    public String fixGeometryTypeByPoint(String tableName) {
        String tempTableName = dsAnalyzeTempSnGenerator.getTempTableName();
        long l = geometryDao.fixGeometryTypeByPoint(tableName);
        if (l <= 0) {
            return tableName;
        }
        return tempTableName;
    }

}
