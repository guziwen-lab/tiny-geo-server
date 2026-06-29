package com.supermap;

import com.supermap.enums.GeomType;
import com.supermap.service.GeometryService;
import com.supermap.type.Column;
import com.supermap.type.TableProcessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public abstract class AbstractAnalysisTask<T extends AnalysisParam> implements AnalysisTask<T> {

    @Autowired
    private GeometryService geometryService;

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
            cleanUpTempTable(context);
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

        setColumns(context);
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

    private void cleanUpTempTable(AnalysisContext<T> context) {
        for (String table : context.getTempTableList()) {
            geometryService.dropTableIfExists(table);
        }
        context.getTempTableList().clear();
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
     * 设置输入图层的字段信息
     */
    private void setColumns(AnalysisContext<T> context) {
        List<LayerInfo> inputLayers = context.getInputLayers();

        for (LayerInfo inputLayer : inputLayers) {
            String tableName = inputLayer.getTableName();
            List<Column> columns = geometryService.listAttrColumns(tableName);
            inputLayer.setColumns(columns);
        }
    }

    /**
     * 修复几何类型
     */
    private void fixGeometry(AnalysisContext<T> context) {
        List<LayerInfo> inputLayers = context.getInputLayers();

        for (LayerInfo layer : inputLayers) {
            GeomType geomType = layer.getGeomType();
            String tableName = layer.getTableName();
            TableProcessResult result = geometryService.normalizeGeometry(tableName, layer.getColumns(), geomType);
            if (result.changed()) {
                context.addTempTable(result.tableName());
                layer.setTableName(result.tableName());
            }
        }
    }

    /**
     * 统一空间参考
     */
    private void unifiedSrid(AnalysisContext<T> context) {
        List<LayerInfo> layers = context.getInputLayers();

        Integer targetSrid = decideTargetSrid(layers);
        for (LayerInfo layer : layers) {
            if (!Objects.equals(layer.getSrid(), targetSrid)) {
                String tempTableName = geometryService.transformTable(layer.getTableName(), targetSrid);
                context.addTempTable(tempTableName);

                layer.setTableName(tempTableName);
                layer.setSrid(targetSrid);
            }
        }
    }

    /**
     * 根据输入图层决定目标空间参考
     *
     * @param layers 输入图层
     * @return 目标空间参考
     */
    protected Integer decideTargetSrid(List<LayerInfo> layers) {
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