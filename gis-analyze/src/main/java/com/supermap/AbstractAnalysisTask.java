package com.supermap;

import com.supermap.common.util.StringUtils;
import com.supermap.dao.GeometryDao;
import com.supermap.enumeration.GeomType;
import com.supermap.service.GeometryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class AbstractAnalysisTask<T extends AnalysisParam> implements AnalysisTask<T> {

    @Autowired
    private GeometryService geometryService;

    @Autowired
    private GeometryDao geometryDao;

    private final List<String> tempTableList = new ArrayList<>();

    @Override
    public AnalysisResult execute(AnalysisContext<T> context) {
        long start = System.currentTimeMillis();

        try {
            validate(context);

            beforeExecute(context);

            AnalysisResult result = doExecute(context);

            long cost = System.currentTimeMillis() - start;
            result.setCost(cost);

            afterExecute(context, result);

            return result;
        } catch (Exception e) {
            onError(context, e);
            throw e;
        } finally {
            logCost(start);
            cleanUpTempTable();
            cleanUp(context);
        }
    }

    /**
     * 参数校验
     * <p>
     * Overlay检查图层是否存在
     * Buffer检查距离参数是否合法
     * Clip检查裁剪图层是否存在
     */
    protected void validate(AnalysisContext<T> context) {
    }

    /**
     * 执行前处理
     */
    protected void beforeExecute(AnalysisContext<T> context) {
        log.info("[{}] analysis start, context={}", getTaskName(), context);

        fixGeometry(context);
        unifiedSrid(context);
    }

    /**
     * 核心分析逻辑
     */
    protected abstract AnalysisResult doExecute(AnalysisContext<T> context);

    /**
     * 执行成功后处理
     */
    protected void afterExecute(AnalysisContext<T> context, AnalysisResult result) {
        log.info("[{}] analysis success, result={}", getTaskName(), result);
    }

    /**
     * 清理工作
     * <p>
     * 删除临时表
     * 删除临时索引
     * 清理中间结果
     */
    protected void cleanUp(AnalysisContext<T> context) {
    }

    private void cleanUpTempTable() {
        for (String table : tempTableList) {
            geometryDao.dropTable(table);
        }
    }

    /**
     * 异常处理
     */
    protected void onError(AnalysisContext<T> context, Exception e) {
        log.error("[{}] analysis failed, context={}", getTaskName(), context, e);
    }

    /**
     * 统计耗时
     */
    protected void logCost(long startTime) {
        long cost = System.currentTimeMillis() - startTime;
        log.info("[{}] analysis finished, cost={} ms", getTaskName(), cost);
    }

    /**
     * 默认任务名称
     */
    protected String getTaskName() {
        return getClass().getSimpleName();
    }

    /**
     * 修复几何类型
     */
    private void fixGeometry(AnalysisContext<T> context) {
        List<LayerInfo> inputLayers = context.getInputLayers();
        List<LayerInfo> newLayers = new ArrayList<>(inputLayers.size());

        for (LayerInfo inputLayer : inputLayers) {
            GeomType geomType = inputLayer.getGeomType();
            String tableName = inputLayer.getTableName();
            String tempTableName = switch (geomType) {
                case MULTI_POLYGON -> geometryService.fixGeometryTypeByMultipolygon(tableName);
                case POINT -> geometryService.fixGeometryTypeByPoint(tableName);
                case MULTI_LINE_STRING -> geometryService.fixGeometryTypeByMultiLineString(tableName);
            };

            if (StringUtils.equals(tableName, tempTableName)) {
                newLayers.add(inputLayer);
            } else {
                tempTableList.add(tempTableName);

                LayerInfo layerInfo = new LayerInfo();
                layerInfo.setTableName(tempTableName);
                layerInfo.setSrid(inputLayer.getSrid());
                layerInfo.setGeomType(inputLayer.getGeomType());
                newLayers.add(layerInfo);
            }
        }

        context.setInputLayers(newLayers);
    }

    /**
     * 统一空间参考
     */
    private void unifiedSrid(AnalysisContext<T> context) {
        List<LayerInfo> layers = context.getInputLayers();

        Integer targetSrid = decideTargetSrid(layers);

        List<LayerInfo> newLayers = new ArrayList<>();

        for (LayerInfo layer : layers) {
            if (Objects.equals(layer.getSrid(), targetSrid)) {
                newLayers.add(layer);
                continue;
            }

            String tempTableName = geometryService.transformTable(layer.getTableName(), targetSrid);
            tempTableList.add(tempTableName);

            LayerInfo layerInfo = new LayerInfo();
            layerInfo.setTableName(tempTableName);
            layerInfo.setSrid(targetSrid);
            layerInfo.setGeomType(layer.getGeomType());
            newLayers.add(layerInfo);
        }

        context.setInputLayers(newLayers);
    }

    /**
     * 根据输入图层决定目标空间参考
     *
     * @param layers 输入图层
     * @return 目标空间参考
     */
    private Integer decideTargetSrid(List<LayerInfo> layers) {
        if (layers == null || layers.isEmpty()) {
            throw new IllegalArgumentException("输入图层不能为空");
        }

        Integer srid = layers.get(0).getSrid();
        if (srid == null || srid <= 0) {
            throw new IllegalArgumentException("图层 " + layers.get(0).getTableName() + " 没有有效的 SRID");
        }

        return srid;
    }

}