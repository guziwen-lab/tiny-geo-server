package com.supermap.task.impl;

import com.supermap.*;
import com.supermap.common.util.StringUtils;
import com.supermap.dao.ExecuteSqlMapper;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import com.supermap.security.SqlInjectionCheck;
import com.supermap.service.GeometryService;
import com.supermap.task.AbstractAnalysisTask;
import com.supermap.task.param.FilterParam;
import com.supermap.util.TableNameUtils;
import com.supermap.util.TempTableNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 属性过滤分析任务
 * <p>
 * 从输入图层中按 SQL WHERE 条件提取子集，生成结果表。
 * 适用于自然资源监测中过滤建设用地、提取其他农用地变化图斑等场景。
 * <p>
 * whereClause 参数格式：直接传入 WHERE 子句字符串（不含 WHERE 关键字），
 * 例如 {@code DLBM IN ('0201','0202') AND TBDLMJ_SPLIT > 0.0001}。
 *
 * @author gzw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FilterAnalysisTask extends AbstractAnalysisTask<FilterParam> {

    private final ExecuteSqlMapper executeSqlMapper;
    private final TempTableNameGenerator tempTableNameGenerator;
    private final GeometryService geometryService;

    @Override
    public AnalysisType getType() {
        return AnalysisType.FILTER;
    }

    /**
     * 属性过滤：保持输入几何类型
     *
     * @param context 分析上下文
     * @return 输入几何类型
     */
    @Override
    public GeomType resultGeomType(AnalysisContext<FilterParam> context) {
        List<LayerInfo> layerInfos = context.getInputLayers();
        return layerInfos.get(0).getGeomType();
    }

    @Override
    public FilterParam buildParam(String whereClause) {
        return new FilterParam(whereClause);
    }

    @Override
    protected void validate(AnalysisContext<FilterParam> context) {
        List<LayerInfo> layers = context.getInputLayers();
        if (layers == null || layers.size() != 1) {
            throw new IllegalArgumentException("属性过滤分析需要且仅需要1个图层");
        }
        if (StringUtils.isEmpty(context.getParam().getWhereClause())) {
            throw new IllegalArgumentException("WHERE 条件不能为空");
        }
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<FilterParam> context) {
        LayerInfo input = context.getInputLayers().get(0);
        String schema = context.getSchema();
        String whereClause = context.getParam().getWhereClause();

        SqlInjectionCheck.checkTableName(input.getTableName());

        String tempTableName = tempTableNameGenerator.getTableName();
        String inputTable = TableNameUtils.getTableNameWithSchema(schema, input.getTableName());
        String tempTable = TableNameUtils.getTableNameWithSchema(schema, tempTableName);

        String sql = """
                CREATE TABLE %s AS
                SELECT * FROM %s
                WHERE %s
                """.formatted(tempTable, inputTable, whereClause);

        log.debug("[FilterAnalysisTask] execute sql: {}", sql);
        executeSqlMapper.executeSql(sql);

        geometryService.addPrimaryKey(schema, tempTableName);
        geometryService.createGistIndex(schema, tempTableName);

        context.addStep(new AnalysisStep(1,
                input.getOriginalTableName(),
                null,
                context.getResultTableName()));

        return finalizeResult(context, tempTableName, "Filter completed");
    }

}
