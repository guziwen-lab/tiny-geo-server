package com.supermap.task;

import com.supermap.*;
import com.supermap.common.util.StringUtils;
import com.supermap.service.*;
import com.supermap.dao.GeometryDao;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.OverlayAlgorithm;
import com.supermap.enums.GeomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OverlayAnalysisTask extends AbstractAnalysisTask<OverlayParam> {

    private final OverlayIntersectService overlayIntersectService;

    private final OverlayClipService overlayClipService;

    private final OverlayEraseService overlayEraseService;

    private final OverlaySymmetricDifferenceService overlaySymmetricDifferenceService;

    private final GeometryDao geometryDao;

    private OverlayAlgorithm overlayAlgorithm;

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
            LayerInfo output;
            switch (overlayAlgorithm) {
                case INTERSECT:
                    output = overlayIntersectService.execute(current, next, context);
                    break;
                case ERASE:
                    output = overlayEraseService.execute(current, next, context);
                    break;
                case CLIP:
                    output = overlayClipService.execute(current, next, context);
                    break;
                case SYMMETRIC_DIFFERENCE:
                    output = overlaySymmetricDifferenceService.execute(current, next, context);
                    break;
                case UNION:
                default:
                    throw new RuntimeException("暂不支持的OverlayType");
            }
            // 添加临时表名到列表，为后续清理
            context.addTempTable(output.getTableName());
            // 添加分析步骤，为后续保存步骤
            context.addStep(new AnalysisStep(stepNo++, current.getTableName(), next.getTableName(), output.getTableName()));
            current = output;
        }

        // 把最后一个临时表改名为结果表
        geometryDao.renameTable(current.getTableName(), resultTableName);

        // 修正最后一步的输出表名为结果表名
        List<AnalysisStep> steps = context.getSteps();
        if (!steps.isEmpty()) {
            AnalysisStep lastStep = steps.get(steps.size() - 1);
            lastStep.setOutputTable(resultTableName);
        }

        geometryDao.analyzeTable(resultTableName);
        long featureCount = geometryDao.getFeatureCount(resultTableName);

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

    @Override
    protected void validate(AnalysisContext<OverlayParam> context) {
        OverlayAlgorithm overlayAlgorithm = context.getParam().getOverlayAlgorithm();
        if (overlayAlgorithm == null)
            throw new IllegalArgumentException("叠加分析类型不能为空");
        this.overlayAlgorithm = overlayAlgorithm;

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
                        overlayAlgorithm + "不支持" + geomType.getOgr2ogrCode()
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

    @Override
    protected void cleanUp(AnalysisContext<OverlayParam> context) {
        for (String table : context.getTempTableList()) {
            geometryDao.dropTableIfExists(table);
        }
    }

}
