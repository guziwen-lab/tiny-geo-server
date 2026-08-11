package com.supermap.analyze.service;

import com.supermap.analyze.helper.UniqueFieldNameHelper;
import com.supermap.gis.service.GeometryService;
import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisParam;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.dao.ExecuteSqlMapper;
import com.supermap.analyze.security.SqlInjectionCheck;
import com.supermap.gis.type.Column;
import com.supermap.idgenerator.TempTableNameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author gzw
 */
@Slf4j
public abstract class AbstractExecuteService<T extends AnalysisParam> implements ExecuteService<T> {

    @Autowired
    protected TempTableNameGenerator tempTableNameGenerator;

    @Autowired
    protected ExecuteSqlMapper executeSqlMapper;

    @Autowired
    private GeometryService geometryService;

    /**
     * 执行
     * <p>1. 生成结果表名</p>
     * <p>2. 执行sql</p>
     * <p>3. 添加主键索引</p>
     * <p>4. 添加gist索引</p>
     *
     * @param current 当前数据集
     * @param next    下一个数据集
     * @param context 分析上下文
     * @return 返回执行后的结果图层信息
     */
    @Override
    public LayerInfo execute(LayerInfo current,
                             LayerInfo next,
                             AnalysisContext<T> context) {
        String resultTableName = tempTableNameGenerator.getTableName();
        return execute(current, next, resultTableName, context);
    }

    /**
     * 执行
     * <p>1. 生成结果表名</p>
     * <p>2. 执行sql</p>
     * <p>3. 添加主键索引</p>
     * <p>4. 添加gist索引</p>
     *
     * @param current         当前数据集
     * @param next            下一个数据集
     * @param resultTableName 结果表名
     * @param context         分析上下文
     * @return 返回执行后的结果图层信息
     */
    @Override
    public LayerInfo execute(LayerInfo current,
                             LayerInfo next,
                             String resultTableName,
                             AnalysisContext<T> context) {
        if (current != null && current.getTableName() != null)
            SqlInjectionCheck.checkTableName(current.getTableName());

        if (next != null && next.getTableName() != null)
            SqlInjectionCheck.checkTableName(next.getTableName());

        String sql = buildExecuteSql(current, next, resultTableName, context);
        log.debug("[taskName: {}] execute sql: {}", context.getTaskName(), sql);

        executeSqlMapper.executeSql(sql);

        // 为结果表添加主键和gist索引
        geometryService.addPrimaryKey(context.getSchema(), resultTableName, context.getPkCol());
        geometryService.createGistIndex(context.getSchema(), resultTableName);

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
     * 构建执行SQL
     *
     * @param current 当前数据集
     * @param next    下一个数据集
     * @param resultTableName 结果表名
     * @param context 分析上下文
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

    protected static List<String> createSingleTableSelectItems(LayerInfo layerInfo,
                                                               AnalysisContext<? extends AnalysisParam> context) {
        List<String> selectItems = new ArrayList<>();

        UniqueFieldNameHelper uniqueFieldNameHelper = new UniqueFieldNameHelper();

        String pkCol = context.getPkCol();
        selectItems.add("row_number() OVER () AS " + pkCol);
        uniqueFieldNameHelper.addUsedName(pkCol);

        List<Column> columns = layerInfo.getColumns();
        for (Column column : columns) {
            String alias = uniqueFieldNameHelper.uniqueFieldName(column.name());
            selectItems.add(
                    "a.\"%s\" AS \"%s\""
                            .formatted(column.name(), alias)
            );
        }
        return selectItems;
    }

}
