package com.supermap.service;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.LayerInfo;
import com.supermap.dao.ExecuteSqlMapper;
import com.supermap.security.SqlInjectionCheck;
import com.supermap.type.Column;
import com.supermap.util.TempTableNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * @author gzw
 */
public abstract class AbstractExecuteService<T extends AnalysisParam> implements ExecuteService<T> {

    @Autowired
    protected TempTableNameGenerator tempTableNameGenerator;

    @Autowired
    protected ExecuteSqlMapper executeSqlMapper;

    @Autowired
    private GeometryService geometryService;

    @Override
    public LayerInfo execute(LayerInfo current, LayerInfo next, AnalysisContext<T> context) {
        SqlInjectionCheck.checkTableName(current.getTableName(), next.getTableName());
        String resultTableName = tempTableNameGenerator.getTableName();

        String sql = buildExecuteSql(current, next, resultTableName, context);

        executeSqlMapper.executeSql(sql);

        // 为结果表添加主键
        geometryService.addPrimaryKey(context.getSchema(), resultTableName);

        LayerInfo resultLayerInfo = new LayerInfo();
        resultLayerInfo.setSrid(context.getSrid());
        resultLayerInfo.setGeomType(context.getGeomType());
        resultLayerInfo.setOriginalTableName(resultTableName);
        resultLayerInfo.setTableName(resultTableName);
        List<Column> columns = geometryService.listAttrColumns(context.getSchema(), resultTableName);
        resultLayerInfo.setColumns(columns);

        return resultLayerInfo;
    }

    /**
     * 执行
     *
     * @param current 当前数据集
     * @param next    下一个数据集
     * @return 可执行SQL
     */
    protected abstract String buildExecuteSql(LayerInfo current,
                                              LayerInfo next,
                                              String resultTableName,
                                              AnalysisContext<T> context);

    protected String getUniqueFieldName(String name, Set<String> usedNames) {
        String result = name;
        int i = 1;

        while (usedNames.contains(result)) {
            result = name + "_" + i++;
        }

        usedNames.add(result);
        return result;
    }

}
