package com.supermap.task;

import com.supermap.*;
import com.supermap.service.*;
import com.supermap.dao.GeometryDao;
import com.supermap.enumeration.AnalysisType;
import com.supermap.enumeration.OverlayAlgorithm;
import com.supermap.enumeration.GeomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    private final List<String> tempTableList = new ArrayList<>();

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
        String current = layers.get(0).getTableName();
        int stepNo = 1;
        for (int i = 1; i < layers.size(); i++) {
            String next = layers.get(i).getTableName();
            String output;
            switch (overlayAlgorithm) {
                case INTERSECT:
                    output = overlayIntersectService.execute(current, next);
                    break;
                case ERASE:
                    output = overlayEraseService.execute(current, next);
                    break;
                case CLIP:
                    output = overlayClipService.execute(current, next);
                    break;
                case SYMMETRIC_DIFFERENCE:
                    output = overlaySymmetricDifferenceService.execute(current, next);
                    break;
                case UNION:
                default:
                    throw new RuntimeException("暂不支持的OverlayType");
            }
            tempTableList.add(output);  // 添加临时表名到列表，为后续清理
            context.addStep(new AnalysisStep(stepNo++, current, next, output)); // 添加分析步骤，为后续保存步骤
            current = output;
        }

        // 把最后一个临时表改名为结果表
        geometryDao.renameTable(current, resultTableName);

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
                .featureCount(featureCount)
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
                        overlayAlgorithm + "不支持" + geomType.getCode()
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
        if (context.isCleanTemp()) {
            for (String table : tempTableList) {
                geometryDao.dropTable(table);
            }
        }
    }

}
