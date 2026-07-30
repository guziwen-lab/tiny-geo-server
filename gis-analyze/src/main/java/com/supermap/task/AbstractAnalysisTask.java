package com.supermap.task;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.AnalysisResult;
import com.supermap.LayerInfo;
import com.supermap.common.util.StringUtils;
import com.supermap.enums.GeomType;
import com.supermap.service.GeometryService;
import com.supermap.type.Column;
import com.supermap.type.TableProcessResult;
import com.supermap.util.TableNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public abstract class AbstractAnalysisTask<T extends AnalysisParam> implements AnalysisTask<T> {

    @Autowired
    private GeometryService geometryService;

    @Override
    public AnalysisResult execute(AnalysisContext<T> context) {
        log.debug("[{}] analysis start, context={}", getTaskName(), context);

        long start = System.currentTimeMillis();

        try {
            log.debug("[{}] validate start", getTaskName());
            validate(context);

            log.debug("[{}] beforeExecute start", getTaskName());
            beforeExecute(context);

            log.debug("[{}] onBeforeExecuted start", getTaskName());
            onBeforeExecuted(context);

            log.debug("[{}] doExecute start", getTaskName());
            AnalysisResult result = doExecute(context);

            long cost = System.currentTimeMillis() - start;
            result.setCost(cost);

            log.debug("[{}] afterExecute start", getTaskName());
            afterExecute(context, result);

            log.debug("[{}] analysis success", getTaskName());
            return result;
        } catch (Exception e) {
            onError(context, e);
            throw e;
        } finally {
            logCost(start);
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
        setResultGeomType(context);
        setColumns(context);
        fixGeometry(context);
        unifiedSrid(context);
    }

    /**
     * 执行前处理完成后
     */
    protected void onBeforeExecuted(AnalysisContext<T> context) {
    }

    /**
     * 核心分析逻辑
     */
    protected abstract AnalysisResult doExecute(AnalysisContext<T> context);

    /**
     * 执行成功后处理
     */
    protected void afterExecute(AnalysisContext<T> context, AnalysisResult result) {
    }

    /**
     * 清理工作
     * <p>
     * 删除临时表
     * 删除临时索引
     * 清理中间结果
     */
    protected void cleanUp(AnalysisContext<T> context) {
        for (String table : context.getTempTableList()) {
            String tableName = TableNameUtils.getTableNameWithSchema(context.getSchema(), table);
            geometryService.dropTableIfExists(tableName);
        }
    }

    /**
     * 异常处理
     */
    protected void onError(AnalysisContext<T> context, Exception e) {
    }

    /**
     * 统计耗时
     */
    protected void logCost(long startTime) {
        long cost = System.currentTimeMillis() - startTime;
        log.debug("[{}] analysis finished, cost={} ms", getTaskName(), cost);
    }

    /**
     * 完成态处理：将最后一个临时表改名为结果表，构建分析结果
     * <p>
     * 子类 doExecute 执行完核心分析逻辑后调用此方法，统一处理：
     * <ol>
     *   <li>把最后一个临时表改名为结果表</li>
     *   <li>从临时表列表中移除已改名的表，避免 cleanUp 时无效 DROP</li>
     *   <li>统计结果表要素数量</li>
     *   <li>构建并返回 AnalysisResult</li>
     * </ol>
     * <p>
     * 注意：子类在创建最后一步 AnalysisStep 时，应直接使用结果表名作为 outputTable，
     * 而非临时表名，以避免“先错后改”。
     *
     * @param context           分析上下文
     * @param lastTempTableName 最后一个临时表名（将被改名为结果表）
     * @param message           结果附加信息
     * @return 分析结果
     */
    protected AnalysisResult finalizeResult(AnalysisContext<T> context,
                                            String lastTempTableName,
                                            String message) {
        String resultTableName = context.getResultTableName();
        String schema = context.getSchema();

        // 把最后一个临时表改名为结果表
        geometryService.renameTable(
                TableNameUtils.getTableNameWithSchema(schema, lastTempTableName),
                resultTableName);

        // 从临时表列表中移除已改名为结果表的表，避免清理时误操作（实际上临时表已经被改名，不会误操作）
        context.getTempTableList().remove(lastTempTableName);

        long featureCount = geometryService.getFeatureCount(
                TableNameUtils.getTableNameWithSchema(schema, resultTableName));

        return AnalysisResult.builder()
                .taskId(context.getTaskId())
                .resultTableName(resultTableName)
                .resultLayerName(StringUtils.isEmpty(context.getResultLayerName())
                        ? resultTableName
                        : context.getResultLayerName())
                .featureCount(featureCount)
                .srid(context.getSrid())
                .geomType(context.getGeomType())
                .message(message)
                .build();
    }

    /**
     * 默认任务名称
     */
    protected String getTaskName() {
        return getClass().getSimpleName();
    }

    /**
     * 判断导出的Geom类型
     */
    private void setResultGeomType(AnalysisContext<T> context) {
        GeomType geomType = resultGeomType(context);
        context.setGeomType(geomType);
    }

    /**
     * 设置输入图层的字段信息
     */
    private void setColumns(AnalysisContext<T> context) {
        List<LayerInfo> inputLayers = context.getInputLayers();
        String schema = context.getSchema();

        for (LayerInfo inputLayer : inputLayers) {
            String tableName = inputLayer.getTableName();
            List<Column> columns = geometryService.listAttrColumns(schema, tableName);
            inputLayer.setColumns(columns);
        }
    }

    /**
     * 修复几何类型
     */
    private void fixGeometry(AnalysisContext<T> context) {
        List<LayerInfo> inputLayers = context.getInputLayers();
        String schema = context.getSchema();

        for (LayerInfo layer : inputLayers) {
            GeomType geomType = layer.getGeomType();
            String tableName = layer.getTableName();
            TableProcessResult result = geometryService.normalizeGeometry(schema, tableName, layer.getColumns(), geomType);
            if (result.changed()) {
                log.debug("[{}] fix geometry, table={}, newTable={}", getTaskName(), tableName, result.tableName());
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
        String schema = context.getSchema();

        Integer targetSrid = decideTargetSrid(layers);
        for (LayerInfo layer : layers) {
            if (!Objects.equals(layer.getSrid(), targetSrid)) {
                String tempTableName = geometryService.transformTable(schema, layer.getTableName(), targetSrid);
                log.debug("[{}] unifiedSrid, table={}, newTable={}", getTaskName(), layer.getTableName(), tempTableName);
                context.addTempTable(tempTableName);

                layer.setTableName(tempTableName);
                layer.setSrid(targetSrid);
            }
        }
        context.setSrid(targetSrid);
    }

    /**
     * 默认取第一个图层的空间参考
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