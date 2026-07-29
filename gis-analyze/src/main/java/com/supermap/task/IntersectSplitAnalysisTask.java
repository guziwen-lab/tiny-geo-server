package com.supermap.task;

import com.supermap.*;
import com.supermap.common.util.CollectionUtils;
import com.supermap.common.util.StringUtils;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import com.supermap.resolver.GeomTypeResolver;
import com.supermap.security.SqlInjectionCheck;
import com.supermap.service.impl.IntersectSplitExecuteService;
import com.supermap.task.param.IntersectSplitParam;
import com.supermap.type.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 相交面积拆分分析任务
 * <p>
 * 对两个面图层执行相交分析，并按相交面积比例拆分指定属性字段。
 * 适用于自然资源监测中 ZT∩DLTB、KFQ_XZQ∩DLTB 等场景。
 * <p>
 * subType 参数格式：{@code A表字段|B表字段}，多个字段用逗号分隔。
 * 例如 {@code jcmj|tbmj,kcmj,tbdlmj} 表示：
 * <ul>
 *   <li>拆分A表（第一个图层）的 jcmj 字段 → jcmj_split</li>
 *   <li>拆分B表（第二个图层）的 tbmj、kcmj、tbdlmj 字段 → tbmj_split、kcmj_split、tbdlmj_split</li>
 * </ul>
 *
 * @author gzw
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class IntersectSplitAnalysisTask extends AbstractAnalysisTask<IntersectSplitParam> {

    private final IntersectSplitExecuteService intersectSplitExecuteService;

    @Override
    public AnalysisType getType() {
        return AnalysisType.INTERSECT_SPLIT;
    }

    @Override
    public GeomType resultGeomType(AnalysisContext<IntersectSplitParam> context) {
        return GeomTypeResolver.resolveOverlay(context.getInputLayers());
    }

    @Override
    public IntersectSplitParam buildParam(String subType) {
        if (StringUtils.isEmpty(subType)) {
            return new IntersectSplitParam(Collections.emptyList(), Collections.emptyList());
        }
        // 格式：A表字段|B表字段，逗号分隔
        String[] parts = subType.split("\\|", 2);
        List<String> splitFieldsA = parseFields(parts[0]);
        List<String> splitFieldsB = parts.length > 1 ? parseFields(parts[1]) : Collections.emptyList();
        return new IntersectSplitParam(splitFieldsA, splitFieldsB);
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<IntersectSplitParam> context) {
        List<LayerInfo> layers = context.getInputLayers();
        // 相交面积拆分仅处理2个图层
        LayerInfo current = layers.get(0);
        LayerInfo next = layers.get(1);

        LayerInfo output = intersectSplitExecuteService.execute(current, next, context);

        // 添加分析步骤：输出表名直接用结果表名
        context.addStep(new AnalysisStep(1,
                current.getOriginalTableName(),
                next.getOriginalTableName(),
                context.getResultTableName()));

        return finalizeResult(context, output.getTableName(), "Intersect with area split completed");
    }

    @Override
    protected void validate(AnalysisContext<IntersectSplitParam> context) {
        List<LayerInfo> layers = context.getInputLayers();

        // 图层数量校验：相交面积拆分仅支持2个图层
        if (layers == null || layers.size() != 2) {
            throw new IllegalArgumentException("相交面积拆分分析需要且仅需要2个图层");
        }

        // 几何类型校验：仅支持面图层
        for (LayerInfo layer : layers) {
            GeomType geomType = layer.getGeomType();
            if (geomType != GeomType.MULTI_POLYGON) {
                throw new IllegalArgumentException(
                        "相交面积拆分分析仅支持面图层(MULTIPOLYGON), 当前: "
                                + geomType.getGeometryName() + ", 图层: " + layer.getTableName());
            }
        }
    }

    /**
     * 校验拆分字段名合法性
     * <p>
     * 由于需要Columns，所以在beforeExecute执行完后再校验
     *
     * @param context 分析上下文
     */
    @Override
    protected void beforeExecute(AnalysisContext<IntersectSplitParam> context) {
        super.beforeExecute(context);

        IntersectSplitParam param = context.getParam();
        List<String> splitFieldsA = param.getSplitFieldsA();
        List<String> splitFieldsB = param.getSplitFieldsB();
        List<LayerInfo> layers = context.getInputLayers();
        validateSplitFields(splitFieldsA, layers.get(0).getColumns(), "A");
        validateSplitFields(splitFieldsB, layers.get(1).getColumns(), "B");
    }

    /**
     * 解析逗号分隔的字段列表
     */
    private List<String> parseFields(String s) {
        if (StringUtils.isEmpty(s)) {
            return Collections.emptyList();
        }
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(f -> !f.isEmpty())
                .toList();
    }

    /**
     * 校验拆分字段名合法性及存在性
     *
     * @param splitFields 待拆分字段列表
     * @param columns     图层实际字段列表
     * @param side        图层标识（A/B），用于错误提示
     */
    private void validateSplitFields(List<String> splitFields, List<Column> columns, String side) {
        if (CollectionUtils.isEmpty(splitFields)) {
            return;
        }
        Set<String> availableNames = new HashSet<>();
        for (Column column : columns) {
            availableNames.add(column.name().toLowerCase());
        }

        SqlInjectionCheck.checkColumnName(splitFields.toArray(new String[0]));

        for (String field : splitFields) {
            if (!availableNames.contains(field.toLowerCase())) {
                throw new IllegalArgumentException(
                        "拆分字段 " + field + " 在" + side + "图层中不存在");
            }
        }
    }

}
