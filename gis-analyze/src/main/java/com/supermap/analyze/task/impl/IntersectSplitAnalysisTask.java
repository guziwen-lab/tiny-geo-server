package com.supermap.analyze.task.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisResult;
import com.supermap.analyze.AnalysisStep;
import com.supermap.analyze.LayerInfo;
import com.supermap.core.util.CollectionUtils;
import com.supermap.core.util.JSON;
import com.supermap.core.util.StringUtils;
import com.supermap.analyze.enums.AnalysisType;
import com.supermap.gis.enums.GeomType;
import com.supermap.analyze.resolver.GeomTypeResolver;
import com.supermap.analyze.helper.SqlInjectionCheckHelper;
import com.supermap.analyze.service.impl.IntersectSplitExecuteService;
import com.supermap.analyze.task.AbstractAnalysisTask;
import com.supermap.analyze.task.param.IntersectSplitParam;
import com.supermap.analyze.task.param.IntersectSplitParam.SplitField;
import com.supermap.gis.type.Column;
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
    public IntersectSplitParam buildParam(String json) {
        if (StringUtils.isEmpty(json)) {
            return new IntersectSplitParam(Collections.emptyList(), Collections.emptyList(), null, null);
        }

        try {
            return JSON.parseObject(json, IntersectSplitParam.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("相交面积拆分分析任务参数解析失败 JSON: " + json, e);
        }
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<IntersectSplitParam> context) {
        List<LayerInfo> layers = context.getInputLayers();
        // 相交面积拆分仅处理2个图层
        LayerInfo current = layers.get(0);
        LayerInfo next = layers.get(1);

        String resultTableName = context.getResultTableName();
        intersectSplitExecuteService.execute(current, next, resultTableName, context);

        // 添加分析步骤
        context.addStep(new AnalysisStep(1,
                current.getOriginalTableName(),
                next.getOriginalTableName(),
                resultTableName));

        return finalizeResult(context, "Intersect with area split completed");
    }

    @Override
    protected void validate(AnalysisContext<IntersectSplitParam> context) {
        IntersectSplitParam param = context.getParam();
        if (param == null)
            throw new IllegalArgumentException("分析参数不能为空");

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

        // 阈值校验
        String intersectAreaThreshold = param.getIntersectAreaThreshold();
        if (intersectAreaThreshold != null) {
            try {
                Integer.parseInt(intersectAreaThreshold);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("相交面积阈值必须为整数");
            }
        }

        String intersectRatioThreshold = param.getIntersectRatioThreshold();
        if (intersectRatioThreshold != null) {
            try {
                Double.parseDouble(intersectRatioThreshold);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("相交比例阈值必须为数字");
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
    protected void onBeforeExecuted(AnalysisContext<IntersectSplitParam> context) {
        IntersectSplitParam param = context.getParam();
        List<SplitField> splitFieldsA = param.getSplitFieldsA();
        List<SplitField> splitFieldsB = param.getSplitFieldsB();

        List<LayerInfo> layers = context.getInputLayers();
        validateSplitFields(splitFieldsA, layers.get(0).getColumns(), "A");
        validateSplitFields(splitFieldsB, layers.get(1).getColumns(), "B");
        validateResultField(param.getRatioFieldA(), "A比例");
        validateResultField(param.getRatioFieldB(), "B比例");
    }

    /**
     * 校验拆分字段名合法性及存在性
     *
     * @param splitFields 待拆分字段列表
     * @param columns     图层实际字段列表
     * @param side        图层标识（A/B），用于错误提示
     */
    private void validateSplitFields(List<SplitField> splitFields, List<Column> columns, String side) {
        if (CollectionUtils.isEmpty(splitFields)) {
            return;
        }
        Set<String> availableNames = new HashSet<>();
        for (Column column : columns) {
            availableNames.add(column.name().toLowerCase());
        }

        SqlInjectionCheckHelper.checkColumnName(splitFields.stream().map(SplitField::getSourceField).toArray(String[]::new));
        SqlInjectionCheckHelper.checkColumnName(splitFields.stream().map(SplitField::getResultField).toArray(String[]::new));

        for (SplitField field : splitFields) {
            if (!availableNames.contains(field.getSourceField().toLowerCase())) {
                throw new IllegalArgumentException(
                        "拆分字段 " + field.getSourceField() + " 在" + side + "图层中不存在");
            }
        }
    }

    private void validateResultField(String field, String label) {
        if (!StringUtils.isEmpty(field)) {
            SqlInjectionCheckHelper.checkColumnName(field);
        }
    }

}
