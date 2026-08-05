package com.supermap.task.impl;

import com.supermap.*;
import com.supermap.common.util.CollectionUtils;
import com.supermap.common.util.JSON;
import com.supermap.common.util.StringUtils;
import com.supermap.dao.ExecuteSqlMapper;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import com.supermap.security.SqlInjectionCheck;
import com.supermap.service.GeometryService;
import com.supermap.task.AbstractAnalysisTask;
import com.supermap.task.param.AttributeCalculateParam;
import com.supermap.task.param.AttributeCalculateParam.CalculatedField;
import com.supermap.type.Column;
import com.supermap.util.TableNameUtils;
import com.supermap.util.TempTableNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性计算分析任务
 * <p>
 * 为输入图层添加一个或多个 SQL 表达式计算字段，生成结果表。
 * 适用于开发区变化情况分析中根据 JSZT2024/JSZT2025 计算 change_type 字段等场景。
 * <p>
 * subType 参数格式：JSON 数组，
 * 例如 {@code [{"name":"change_type","expr":"CASE WHEN ... END"}]}
 *
 * @author gzw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttributeCalculateAnalysisTask extends AbstractAnalysisTask<AttributeCalculateParam> {

    private final ExecuteSqlMapper executeSqlMapper;
    private final GeometryService geometryService;

    @Override
    public AnalysisType getType() {
        return AnalysisType.ATTRIBUTE_CALCULATE;
    }

    /**
     * 属性计算：保持输入几何类型
     *
     * @param context 分析上下文
     * @return 输出几何类型
     */
    @Override
    public GeomType resultGeomType(AnalysisContext<AttributeCalculateParam> context) {
        List<LayerInfo> layerInfos = context.getInputLayers();
        return layerInfos.get(0).getGeomType();
    }

    /**
     * jsonArr 参数格式：JSON 数组，
     * 例如 {@code [{"name":"change_type","expression":"CASE WHEN ... END"}]}
     *
     * @param jsonArr 构建参数
     * @return 构建的参数对象
     */
    @Override
    public AttributeCalculateParam buildParam(String jsonArr) {
        if (StringUtils.isEmpty(jsonArr)) {
            return new AttributeCalculateParam(List.of());
        }
        try {
            List<CalculatedField> fields = JSON.parseArray(jsonArr, CalculatedField.class);
            return new AttributeCalculateParam(fields);
        } catch (Exception e) {
            throw new IllegalArgumentException("解析属性计算参数失败: " + e.getMessage(), e);
        }
    }

    @Override
    protected void validate(AnalysisContext<AttributeCalculateParam> context) {
        List<LayerInfo> layers = context.getInputLayers();
        if (layers == null || layers.size() != 1) {
            throw new IllegalArgumentException("属性计算分析需要且仅需要1个图层");
        }
        List<CalculatedField> fields = context.getParam().getFields();
        if (CollectionUtils.isEmpty(fields)) {
            throw new IllegalArgumentException("计算字段列表不能为空");
        }
        for (CalculatedField field : fields) {
            if (StringUtils.isEmpty(field.name())) {
                throw new IllegalArgumentException("计算字段名不能为空");
            }
            if (StringUtils.isEmpty(field.expression())) {
                throw new IllegalArgumentException("计算字段表达式不能为空: " + field.name());
            }
        }
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<AttributeCalculateParam> context) {
        LayerInfo input = context.getInputLayers().get(0);
        String tableName = input.getTableName();
        String schema = context.getSchema();
        String resultTableName = context.getResultTableName();

        SqlInjectionCheck.checkTableName(tableName, resultTableName);

        List<CalculatedField> fields = context.getParam().getFields();
        List<String> fieldNames = fields.stream().map(CalculatedField::name).toList();
        SqlInjectionCheck.checkColumnName(fieldNames.toArray(new String[0]));

        String inputTable = TableNameUtils.getTableNameWithSchema(schema, tableName);
        String resultTable = TableNameUtils.getTableNameWithSchema(schema, resultTableName);

        List<String> selectItems = new ArrayList<>();
        selectItems.add("row_number() OVER () AS id");

        for (Column column : input.getColumns()) {
            selectItems.add("\"%s\"".formatted(column.name()));
        }
        for (CalculatedField field : fields) {
            selectItems.add("(%s) AS \"%s\"".formatted(field.expression(), field.name()));
        }
        selectItems.add("geom");

        String sql = """
                CREATE TABLE %s AS
                SELECT
                %s
                FROM %s
                """.formatted(resultTable, String.join(",\n", selectItems), inputTable);

        log.debug("[taskName: {}] execute sql: {}", context.getTaskName(), sql);
        executeSqlMapper.executeSql(sql);

        geometryService.addPrimaryKey(schema, resultTableName, "serial_id");
        geometryService.createGistIndex(schema, resultTableName);

        context.addStep(new AnalysisStep(1,
                input.getOriginalTableName(),
                null,
                resultTableName));

        return finalizeResult(context, resultTableName, "Attribute calculate completed");
    }

}
