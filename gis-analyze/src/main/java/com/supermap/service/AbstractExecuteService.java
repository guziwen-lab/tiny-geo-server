package com.supermap.service;

import com.supermap.dao.ExecuteSqlMapper;
import com.supermap.dao.GeometryDao;
import com.supermap.security.SqlInjectionCheck;
import com.supermap.util.DsTempSnGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author gzw
 */
public abstract class AbstractExecuteService {

    @Autowired
    protected DsTempSnGenerator dsTempSnGenerator;

    @Autowired
    protected ExecuteSqlMapper executeSqlMapper;

    @Autowired
    protected GeometryDao geometryDao;

    public String execute(String current, String next) {
        SqlInjectionCheck.checkTableName(current, next);
        return executeInternal(current, next);
    }

    /**
     * 执行
     *
     * @param current 当前数据集
     * @param next    下一个数据集
     * @return 执行结果表名
     */
    protected abstract String executeInternal(String current, String next);

    protected String uniqueFieldName(String name, Set<String> usedNames) {
        String result = name;
        int i = 1;

        while (usedNames.contains(result)) {
            result = name + "_" + i++;
        }

        usedNames.add(result);
        return result;
    }

    protected String getResultTableName() {
        return "ds_temp_" + dsTempSnGenerator.generate();
    }

}
