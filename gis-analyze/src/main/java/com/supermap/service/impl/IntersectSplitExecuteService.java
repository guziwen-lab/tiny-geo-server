package com.supermap.service.impl;

import com.supermap.AnalysisContext;
import com.supermap.LayerInfo;
import com.supermap.service.AbstractExecuteService;
import com.supermap.service.GeometryExpression;
import com.supermap.task.param.IntersectSplitParam;
import com.supermap.task.param.IntersectSplitParam.SplitField;
import com.supermap.type.Column;
import com.supermap.util.TableNameUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 相交面积拆分执行服务
 * <p>
 * 对两个面图层执行几何相交（ST_Intersection），并按相交面积比例拆分指定属性字段，
 * 生成 {@code {field}_split} 拆分字段。这是自然资源监测等面积统计场景的核心操作：
 * 相交后每个碎块按「相交面积 / 原图斑面积」的比例分配源要素的属性值（如监测面积、
 * 图斑面积等），确保后续 SUM 统计不重复计算。
 *
 * @author gzw
 */
@Service
public class IntersectSplitExecuteService extends AbstractExecuteService<IntersectSplitParam> {

    @Override
    protected String buildExecuteSql(LayerInfo current, LayerInfo next, String resultTableName,
                                     AnalysisContext<IntersectSplitParam> context) {
        IntersectSplitParam param = context.getParam();
        List<SplitField> splitFieldsA = param.getSplitFieldsA();
        List<SplitField> splitFieldsB = param.getSplitFieldsB();

        Set<String> usedNames = new HashSet<>();
        List<String> selectItems = new ArrayList<>();

        // 源要素主键用于面积守恒校验及定位异常图斑。
        selectItems.add("a.id AS source_a_id");
        usedNames.add("source_a_id");
        selectItems.add("b.id AS source_b_id");
        usedNames.add("source_b_id");

        // A表（当前图层）全部属性字段
        for (Column column : current.getColumns()) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            selectItems.add("a.\"%s\" AS \"%s\"".formatted(column.name(), alias));
        }

        // B表（叠加图层）全部属性字段
        for (Column column : next.getColumns()) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            selectItems.add("b.\"%s\" AS \"%s\"".formatted(column.name(), alias));
        }

        // 面积比例拆分字段
        // ratio = 相交面积 / 原图斑面积，按比例分配属性值
        String intersectionArea = "ST_Area(ST_Intersection(a.geom, b.geom))";
        String ratioA = intersectionArea + " / NULLIF(ST_Area(a.geom), 0)";
        String ratioB = intersectionArea + " / NULLIF(ST_Area(b.geom), 0)";

        // A表拆分字段：按A表原图斑面积比例拆分
        for (SplitField field : splitFieldsA) {
            String splitAlias = getUniqueFieldName(field.resultField(), usedNames);
            selectItems.add("COALESCE(a.\"%s\", 0) * (%s) AS \"%s\""
                    .formatted(field.sourceField(), ratioA, splitAlias));
        }

        // B表拆分字段：按B表原图斑面积比例拆分
        for (SplitField field : splitFieldsB) {
            String splitAlias = getUniqueFieldName(field.resultField(), usedNames);
            selectItems.add("COALESCE(b.\"%s\", 0) * (%s) AS \"%s\""
                    .formatted(field.sourceField(), ratioB, splitAlias));
        }

        if (param.getRatioFieldA() != null && !param.getRatioFieldA().isBlank()) {
            String ratioAlias = getUniqueFieldName(param.getRatioFieldA(), usedNames);
            selectItems.add("(%s) AS \"%s\"".formatted(ratioA, ratioAlias));
        }
        if (param.getRatioFieldB() != null && !param.getRatioFieldB().isBlank()) {
            String ratioAlias = getUniqueFieldName(param.getRatioFieldB(), usedNames);
            selectItems.add("(%s) AS \"%s\"".formatted(ratioB, ratioAlias));
        }

        // 几何字段
        String geomExpr = GeometryExpression.wrap(
                "ST_Intersection(a.geom, b.geom)", context.getGeomType(), context.getSrid());
        selectItems.add(geomExpr + " AS geom");

        // 构建 CREATE TABLE AS SELECT
        // JOIN 条件：ST_Intersects 走空间索引初筛，ST_Relate('2********') 要求内部相交且交集为二维面，
        // 排除仅共享边界（共边/共点）导致的空面结果；
        // 外层 NOT ST_IsEmpty 兜底过滤浮点精度产生的空几何，row_number 在过滤后生成以保证 id 连续
        String currentTable = TableNameUtils.getTableNameWithSchema(context.getSchema(), current.getTableName());
        String nextTable = TableNameUtils.getTableNameWithSchema(context.getSchema(), next.getTableName());
        String resultTable = TableNameUtils.getTableNameWithSchema(context.getSchema(), resultTableName);

        return """
                CREATE TABLE %s AS
                SELECT row_number() OVER () AS serial_id, t.*
                FROM (
                SELECT
                %s
                FROM %s a
                JOIN %s b
                  ON ST_Intersects(a.geom, b.geom)
                 AND ST_Relate(a.geom, b.geom, '2********')
                ) t
                WHERE NOT ST_IsEmpty(t.geom)
                """.formatted(resultTable, String.join(",\n", selectItems), currentTable, nextTable);
    }

}
