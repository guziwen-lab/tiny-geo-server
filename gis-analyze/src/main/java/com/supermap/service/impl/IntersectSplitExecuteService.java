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
        List<String> t1SelectItems = new ArrayList<>();
        List<String> t2SelectItems = new ArrayList<>();
        List<String> outerSelectItems = new ArrayList<>();

        /*--------------------- t1层查询字段 ---------------------*/
        // 源要素主键用于面积守恒校验及定位异常图斑。
        t1SelectItems.add("a.id AS source_a_id");
        usedNames.add("source_a_id");
        t1SelectItems.add("b.id AS source_b_id");
        usedNames.add("source_b_id");

        // A表（当前图层）全部属性字段
        for (Column column : current.getColumns()) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            t1SelectItems.add("a.\"%s\" AS \"%s\"".formatted(column.name(), alias));
        }

        // B表（叠加图层）全部属性字段
        for (Column column : next.getColumns()) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            t1SelectItems.add("b.\"%s\" AS \"%s\"".formatted(column.name(), alias));
        }

        // A表图形面积
        String areaA = "ST_Area(a.geom) AS a_area";
        t1SelectItems.add(areaA);

        // B表图形面积
        String areaB = "ST_Area(b.geom) AS b_area";
        t1SelectItems.add(areaB);

        // 相交图形
        String intersectionGeom = "ST_Intersection(a.geom, b.geom) AS inter_geom";
        t1SelectItems.add(intersectionGeom);

        /*--------------------- t2层查询字段 ---------------------*/
        // t1全部字段
        String t1All = "t1.*";
        t2SelectItems.add(t1All);
        // 相交面积
        String intersectionArea = "ST_Area(inter_geom) AS inter_area";
        t2SelectItems.add(intersectionArea);

        /*--------------------- 最外层查询字段 ---------------------*/
        String id = "row_number() OVER () AS serial_id";
        outerSelectItems.add(id);
        String t2All = "t2.*";
        outerSelectItems.add(t2All);

        // ratio = 相交面积 / 原图斑面积，按比例分配属性值
        String interArea = "t2.inter_area";
        String ratioA = interArea + " / NULLIF(t2.a_area, 0)";
        String ratioB = interArea + " / NULLIF(t2.b_area, 0)";

        // A表拆分字段：按A表原图斑面积比例拆分
        for (SplitField field : splitFieldsA) {
            String splitAlias = getUniqueFieldName(field.resultField(), usedNames);
            outerSelectItems.add("COALESCE(t2.\"%s\", 0) * (%s) AS \"%s\""
                    .formatted(field.sourceField(), ratioA, splitAlias));
        }

        // B表拆分字段：按B表原图斑面积比例拆分
        for (SplitField field : splitFieldsB) {
            String splitAlias = getUniqueFieldName(field.resultField(), usedNames);
            outerSelectItems.add("COALESCE(t2.\"%s\", 0) * (%s) AS \"%s\""
                    .formatted(field.sourceField(), ratioB, splitAlias));
        }

        // A表比例字段
        if (param.getRatioFieldA() != null && !param.getRatioFieldA().isBlank()) {
            String ratioAlias = getUniqueFieldName(param.getRatioFieldA(), usedNames);
            outerSelectItems.add("(%s) AS \"%s\"".formatted(ratioA, ratioAlias));
        }

        // B表比例字段
        if (param.getRatioFieldB() != null && !param.getRatioFieldB().isBlank()) {
            String ratioAlias = getUniqueFieldName(param.getRatioFieldB(), usedNames);
            outerSelectItems.add("(%s) AS \"%s\"".formatted(ratioB, ratioAlias));
        }

        // 最终geom
        String geomExpr = GeometryExpression.wrap("t2.inter_geom",
                context.getGeomType(),
                context.getSrid());
        outerSelectItems.add(geomExpr + " AS geom");

        // 构建 CREATE TABLE AS SELECT
        // JOIN 条件：ST_Intersects 走空间索引初筛，ST_Relate('2********') 要求内部相交且交集为二维面，
        // 排除仅共享边界（共边/共点）导致的空面结果；
        // 外层 NOT ST_IsEmpty 兜底过滤浮点精度产生的空几何，row_number 在过滤后生成以保证 id 连续
        String currentTable = TableNameUtils.getTableNameWithSchema(context.getSchema(), current.getTableName());
        String nextTable = TableNameUtils.getTableNameWithSchema(context.getSchema(), next.getTableName());
        String resultTable = TableNameUtils.getTableNameWithSchema(context.getSchema(), resultTableName);

        return """
                CREATE TABLE %s AS
                SELECT
                %s
                FROM (
                SELECT
                %s
                FROM (SELECT
                %s
                FROM %s a
                JOIN %s b
                  ON ST_Intersects(a.geom, b.geom)
                 AND ST_Relate(a.geom, b.geom, '2********')) t1) t2
                WHERE NOT ST_IsEmpty(ST_CollectionExtract(t2.inter_geom, %s))
                """.formatted(resultTable,
                String.join(",\n", outerSelectItems),
                String.join(",\n", t2SelectItems),
                String.join(",\n", t1SelectItems),
                currentTable,
                nextTable,
                context.getGeomType().getCollectionExtractType());
    }

}
