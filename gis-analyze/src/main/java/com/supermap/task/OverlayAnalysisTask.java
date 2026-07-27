package com.supermap.task;

import com.supermap.*;
import com.supermap.common.util.CollectionUtils;
import com.supermap.common.util.StringUtils;
import com.supermap.service.*;
import com.supermap.dao.GeometryDao;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.OverlayAlgorithm;
import com.supermap.enums.GeomType;
import com.supermap.task.param.OverlayParam;
import com.supermap.util.TableNameUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class OverlayAnalysisTask extends AbstractAnalysisTask<OverlayParam> {

    private final List<AbstractOverlayExecuteService> overlayExecuteServices;

    private Map<OverlayAlgorithm, AbstractOverlayExecuteService> overlayExecuteServiceMap;

    private final GeometryDao geometryDao;

    @PostConstruct
    public void init() {
        overlayExecuteServiceMap = CollectionUtils.toMap(overlayExecuteServices,
                AbstractOverlayExecuteService::getAlgorithm,
                Function.identity());
    }

    @Override
    public AnalysisType getType() {
        return AnalysisType.OVERLAY;
    }

    @Override
    public OverlayParam buildParam(String subType) {
        return new OverlayParam(OverlayAlgorithm.valueOf(subType));
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<OverlayParam> context) {
        String resultTableName = context.getResultTableName();

        List<LayerInfo> layers = context.getInputLayers();
        LayerInfo current = layers.get(0);
        int stepNo = 1;
        for (int i = 1; i < layers.size(); i++) {
            LayerInfo next = layers.get(i);
            LayerInfo output = getOverlayExecuteService(context.getParam().getOverlayAlgorithm())
                    .execute(current, next, context);
            // 添加临时表名到列表，为后续清理
            context.addTempTable(output.getTableName());
            // 添加分析步骤，为后续保存步骤
            context.addStep(new AnalysisStep(stepNo++,
                    current.getOriginalTableName(),
                    next.getOriginalTableName(),
                    output.getOriginalTableName()));
            current = output;
        }

        // 把最后一个临时表改名为结果表
        geometryDao.renameTable(TableNameUtils.getTableNameWithSchema(context.getSchema(), current.getTableName()),
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
                .message("Overlay analysis completed")
                .build();
    }

    private AbstractOverlayExecuteService getOverlayExecuteService(OverlayAlgorithm overlayAlgorithm) {
        AbstractOverlayExecuteService service = overlayExecuteServiceMap.get(overlayAlgorithm);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported overlay algorithm: " + overlayAlgorithm);
        }
        return service;
    }

    @Override
    protected void validate(AnalysisContext<OverlayParam> context) {
        OverlayAlgorithm overlayAlgorithm = context.getParam().getOverlayAlgorithm();
        if (overlayAlgorithm == null)
            throw new IllegalArgumentException("叠加分析类型不能为空");

        List<LayerInfo> layers = context.getInputLayers();

        // 图层数量校验
        if (layers == null || layers.size() < 2) {
            throw new IllegalArgumentException("叠加分析至少需要2个图层");
        }

        // 几何类型兼容性校验
        for (LayerInfo layer : layers) {
            GeomType geomType = layer.getGeomType();
            if (!isCompatible(overlayAlgorithm, layer.getGeomType())) {
                throw new IllegalArgumentException(
                        overlayAlgorithm + "不支持" + geomType.getGeometryName()
                        + "类型, 图层: " + layer.getTableName());
            }
        }
    }

    private boolean isCompatible(OverlayAlgorithm overlayAlgorithm, GeomType geomType) {
        return switch (overlayAlgorithm) {
            case INTERSECT, UNION, SYMMETRIC_DIFFERENCE, IDENTITY ->
                    geomType == GeomType.MULTI_POLYGON;
            case CLIP, ERASE ->
                    geomType == GeomType.MULTI_POLYGON
                    || geomType == GeomType.MULTI_LINE_STRING;
        };
    }

}
