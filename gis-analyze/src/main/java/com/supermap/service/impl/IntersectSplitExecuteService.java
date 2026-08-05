package com.supermap.service.impl;

import com.supermap.AnalysisContext;
import com.supermap.LayerInfo;
import com.supermap.common.util.CollectionUtils;
import com.supermap.service.AbstractExecuteService;
import com.supermap.service.GeometryExpression;
import com.supermap.task.param.IntersectSplitParam;
import com.supermap.task.param.IntersectSplitParam.SplitField;
import com.supermap.type.Column;
import com.supermap.util.TableNameUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

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
        Map<String, SplitField> splitFieldsAMap = CollectionUtils
                .toMap(splitFieldsA, SplitField::getSourceField, Function.identity());
        Map<String, SplitField> splitFieldsBMap = CollectionUtils
                .toMap(splitFieldsB, SplitField::getSourceField, Function.identity());

        Set<String> usedNames = new HashSet<>();
        List<String> t1SelectItems = new ArrayList<>();
        List<String> t2SelectItems = new ArrayList<>();
        List<String> outerSelectItems = new ArrayList<>();
        Set<String> originalAttrs = new HashSet<>();    // 需要保留的原表属性字段

        /*--------------------- t1层查询字段 ---------------------*/
        // 源要素主键用于面积守恒校验及定位异常图斑。
        String sourceAId = getUniqueFieldName("source_a_id", usedNames);
        originalAttrs.add(sourceAId);
        t1SelectItems.add("a.\"id\" AS %s".formatted(sourceAId));
        String sourceBId = getUniqueFieldName("source_b_id", usedNames);
        originalAttrs.add(sourceBId);
        t1SelectItems.add("b.\"id\" AS %s".formatted(sourceBId));

        // A表（当前图层）全部属性字段
        for (Column column : current.getColumns()) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            originalAttrs.add(alias);
            t1SelectItems.add("a.\"%s\" AS \"%s\"".formatted(column.name(), alias));

            // 如果A表的字段和B表的字段冲突了，B表的该字段会被改名，后续计算拆分时需要用新字段名。实际上应该只有B表才会被改名，此处兜底冗余一下。
            if (splitFieldsAMap.containsKey(column.name())) {
                SplitField splitField = splitFieldsAMap.get(column.name());
                splitField.setSourceField(alias);
            }
        }

        // B表（叠加图层）全部属性字段
        for (Column column : next.getColumns()) {
            String alias = getUniqueFieldName(column.name(), usedNames);
            originalAttrs.add(alias);
            t1SelectItems.add("b.\"%s\" AS \"%s\"".formatted(column.name(), alias));

            // 如果B表的字段和A表的字段冲突了，B表的该字段会被改名，后续计算拆分时需要用新字段名
            if (splitFieldsBMap.containsKey(column.name())) {
                SplitField splitField = splitFieldsBMap.get(column.name());
                splitField.setSourceField(alias);
            }
        }

        // A表图形面积
        String areaA = "ST_Area(a.geom) AS %s".formatted(getUniqueFieldName("a_area", usedNames));
        t1SelectItems.add(areaA);

        // B表图形面积
        String areaB = "ST_Area(b.geom) AS %s".formatted(getUniqueFieldName("b_area", usedNames));
        t1SelectItems.add(areaB);

        // 相交图形
        String interGeom = getUniqueFieldName("inter_geom", usedNames);
        String intersectionGeom = "ST_Intersection(a.geom, b.geom) AS %s"
                .formatted(interGeom);
        t1SelectItems.add(intersectionGeom);

        /*--------------------- t2层查询字段 ---------------------*/
        // t1全部字段
        String t1All = "t1.*";
        t2SelectItems.add(t1All);
        // 相交面积
        String interArea = getUniqueFieldName("inter_area", usedNames);
        String intersectionArea = "ST_Area(%s) AS %s".formatted(interGeom, interArea);
        t2SelectItems.add(intersectionArea);
        getUniqueFieldName("inter_area", usedNames);

        /*--------------------- 最外层查询字段 ---------------------*/
        String id = "row_number() OVER () AS %s".formatted(context.getPkCol());
        outerSelectItems.add(id);

        // 需要保留的原表属性字段
        for (String originalAttr : originalAttrs) {
            outerSelectItems.add("t2.%s".formatted(originalAttr));
        }

        // ratio = 相交面积 / 原图斑面积，按比例分配属性值
        String t2InterArea = "t2.%s".formatted(interArea);
        String ratioA = t2InterArea + " / NULLIF(t2.a_area, 0)";
        String ratioB = t2InterArea + " / NULLIF(t2.b_area, 0)";

        // A表拆分字段：按A表原图斑面积比例拆分
        /*
            COALESCE(t2."jcmj", 0) * (t2.inter_area / NULLIF(t2.a_area, 0)) AS "jcmj_split",
            COALESCE(t2."tbmj", 0) * (t2.inter_area / NULLIF(t2.b_area, 0)) AS "tbmj_split",
            COALESCE(t2."kcmj", 0) * (t2.inter_area / NULLIF(t2.b_area, 0)) AS "kcmj_split",
         */
        for (SplitField field : splitFieldsA) {
            String splitAlias = getUniqueFieldName(field.getResultField(), usedNames);
            outerSelectItems.add("COALESCE(t2.\"%s\", 0) * (%s) AS \"%s\""
                    .formatted(field.getSourceField(), ratioA, splitAlias));
        }

        // B表拆分字段：按B表原图斑面积比例拆分
        for (SplitField field : splitFieldsB) {
            String splitAlias = getUniqueFieldName(field.getResultField(), usedNames);
            outerSelectItems.add("COALESCE(t2.\"%s\", 0) * (%s) AS \"%s\""
                    .formatted(field.getSourceField(), ratioB, splitAlias));
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
        String geomExpr = GeometryExpression.wrap("t2.%s".formatted(interGeom), context.getGeomType(), context.getSrid());
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
                WHERE NOT ST_IsEmpty(ST_CollectionExtract(t2.%s, %s))
                """.formatted(resultTable,
                String.join(",\n", outerSelectItems),
                String.join(",\n", t2SelectItems),
                String.join(",\n", t1SelectItems),
                currentTable,
                nextTable,
                interGeom,
                context.getGeomType().getCollectionExtractType());
    }

}
