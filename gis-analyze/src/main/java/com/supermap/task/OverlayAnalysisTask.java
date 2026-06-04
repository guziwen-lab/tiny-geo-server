package com.supermap.task;

import com.supermap.*;
import com.supermap.service.OverlayService;
import com.supermap.dao.GeometryDao;
import com.supermap.enumeration.AnalysisType;
import com.supermap.enumeration.OverlayType;
import com.supermap.enumeration.GeomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class OverlayAnalysisTask extends AbstractAnalysisTask<OverlayParam> {

    private final OverlayService overlayService;

    private final GeometryDao geometryDao;

    private final List<String> tempTableList = new ArrayList<>();

    @Override
    public AnalysisType getType() {
        return AnalysisType.OVERLAY;
    }

    @Override
    public OverlayParam buildParam(String subType) {
        return new OverlayParam(OverlayType.valueOf(subType));
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<OverlayParam> context) {
        String resultTableName = context.getResultTableName();
        OverlayType overlayType = context.getParam().getOverlayType();

        List<LayerInfo> layers = context.getInputLayers();
        String current = layers.get(0).getTableName();
        int stepNo = 1;
        for (int i = 1; i < layers.size(); i++) {
            String next = layers.get(i).getTableName();
            String output;
            switch (overlayType) {
                case INTERSECT:
                    output = executeIntersect(current, next);
                    tempTableList.add(output);
                    context.addStep(new AnalysisStep(stepNo++, current, next, output));
                    current = output;
                    break;
            }
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
        List<LayerInfo> layers = context.getInputLayers();

        // 图层数量校验
        if (layers == null || layers.size() < 2) {
            throw new IllegalArgumentException("叠加分析至少需要2个图层");
        }

        // SRID 一致性校验
        Integer baseSrid = layers.get(0).getSrid();
        for (int i = 1; i < layers.size(); i++) {
            if (!Objects.equals(baseSrid, layers.get(i).getSrid())) {
                throw new IllegalArgumentException(
                        "图层SRID不一致: " + layers.get(0).getTableName()
                        + "(SRID=" + baseSrid + ") 与 "
                        + layers.get(i).getTableName()
                        + "(SRID=" + layers.get(i).getSrid() + ")");
            }
        }

        // 几何类型兼容性校验
        OverlayType overlayType = context.getParam().getOverlayType();
        for (LayerInfo layer : layers) {
            GeomType geomType = GeomType.of(layer.getGeomType());
            if (geomType == null) {
                throw new IllegalArgumentException(
                        "不支持的几何类型: " + layer.getGeomType()
                        + ", 图层: " + layer.getTableName());
            }
            if (!isCompatible(overlayType, geomType)) {
                throw new IllegalArgumentException(
                        overlayType + "不支持" + geomType.getCode()
                        + "类型, 图层: " + layer.getTableName());
            }
        }
    }

    private boolean isCompatible(OverlayType overlayType, GeomType geomType) {
        return switch (overlayType) {
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

    private String executeIntersect(String current, String next) {
        return overlayService.executeOverlay(current, next);
    }

}
