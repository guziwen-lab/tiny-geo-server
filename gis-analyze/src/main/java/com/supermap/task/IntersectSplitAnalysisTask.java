package com.supermap.task;

import com.supermap.*;
import com.supermap.common.util.StringUtils;
import com.supermap.dao.GeometryDao;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import com.supermap.service.impl.IntersectSplitExecuteService;
import com.supermap.task.param.IntersectSplitParam;
import com.supermap.util.TableNameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private final GeometryDao geometryDao;

    @Override
    public AnalysisType getType() {
        return AnalysisType.INTERSECT_SPLIT;
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
        String resultTableName = context.getResultTableName();

        List<LayerInfo> layers = context.getInputLayers();
        // 相交面积拆分仅处理2个图层
        LayerInfo current = layers.get(0);
        LayerInfo next = layers.get(1);

        LayerInfo output = intersectSplitExecuteService.execute(current, next, context);
        // 添加临时表名到列表，为后续清理
        context.addTempTable(output.getTableName());
        // 添加分析步骤，为后续保存步骤
        context.addStep(new AnalysisStep(1,
                current.getOriginalTableName(),
                next.getOriginalTableName(),
                output.getOriginalTableName()));

        // 把临时表改名为结果表
        geometryDao.renameTable(
                TableNameUtils.getTableNameWithSchema(context.getSchema(), output.getTableName()),
                resultTableName);

        // 修正最后一步的输出表名为结果表名
        List<AnalysisStep> steps = context.getSteps();
        if (!steps.isEmpty()) {
            AnalysisStep lastStep = steps.get(steps.size() - 1);
            lastStep.setOutputTable(resultTableName);
        }

        long featureCount = geometryDao.getFeatureCount(
                TableNameUtils.getTableNameWithSchema(context.getSchema(), resultTableName));

        return AnalysisResult.builder()
                .taskId(context.getTaskId())
                .resultTableName(resultTableName)
                .resultLayerName(StringUtils.isEmpty(context.getResultLayerName())
                        ? resultTableName
                        : context.getResultLayerName())
                .featureCount(featureCount)
                .srid(context.getSrid())
                .geomType(context.getGeomType())
                .message("Intersect with area split completed")
                .build();
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
     * 解析逗号分隔的字段列表
     */
    private List<String> parseFields(String s) {
        if (s == null || s.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(f -> !f.isEmpty())
                .toList();
    }

}
