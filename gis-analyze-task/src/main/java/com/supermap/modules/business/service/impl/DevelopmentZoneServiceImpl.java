package com.supermap.modules.business.service.impl;

import com.supermap.AnalysisResult;
import com.supermap.LayerInfo;
import com.supermap.config.DatasetProperties;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.UploadStatus;
import com.supermap.modules.business.constant.BusinessConstants;
import com.supermap.modules.business.enums.Caliber;
import com.supermap.modules.business.service.DevelopmentZoneService;
import com.supermap.support.AnalysisExecutor;
import com.supermap.support.LayerInfoBuilder;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.service.GeometryService;
import com.supermap.task.param.AttributeCalculateParam;
import com.supermap.task.param.AttributeCalculateParam.CalculatedField;
import com.supermap.task.param.FilterParam;
import com.supermap.task.param.IntersectSplitParam;
import com.supermap.util.TableNameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 国家级开发区分析服务实现
 * <p>
 * 输出10类矢量成果：建设状态、建设密度、要素名称、变化情况、土地利用现状（各分同口径/非同口径）。
 *
 * @author gzw
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DevelopmentZoneServiceImpl implements DevelopmentZoneService {

    private final AnalysisExecutor analysisExecutor;
    private final DatasetService datasetService;
    private final DatasetProperties datasetProperties;
    private final GeometryService geometryService;

    // ======================== 1-2. 建设状态 ========================

    @Override
    public Long exportConstructionStatus(Long kfqDatasetId) {
        log.info("[开发区-建设状态] 直接返回KFQ数据集, kfqDatasetId={}", kfqDatasetId);
        DatasetEntity kfqDataset = datasetService.getById(kfqDatasetId);
        if (kfqDataset == null) {
            throw new IllegalArgumentException("KFQ数据集不存在: " + kfqDatasetId);
        }
        return kfqDatasetId;
    }

    // ======================== 3-4. 建设密度 ========================

    @Override
    public Long analyzeConstructionDensity(Long jdDatasetId, Long dltbDatasetId) {
        log.info("[开发区-建设密度] 开始, jdDatasetId={}, dltbDatasetId={}", jdDatasetId, dltbDatasetId);

        String schema = datasetProperties.getSchema();
        DatasetEntity jdDataset = getDataset(jdDatasetId);
        DatasetEntity dltbDataset = getDataset(dltbDatasetId);

        // 步骤1：过滤DLTB为建设用地
        String whereClause = buildJsydFilterClause();
        FilterParam filterParam = new FilterParam(whereClause);
        LayerInfo dltbLayer = LayerInfoBuilder.fromDatasetEntity(dltbDataset);

        AnalysisResult step1Result = analysisExecutor.execute(
                AnalysisType.FILTER,
                List.of(dltbLayer),
                filterParam,
                schema
        );
        log.info("[开发区-建设密度] 步骤1完成(过滤建设用地), 结果表={}, 要素数={}",
                step1Result.getResultTableName(), step1Result.getFeatureCount());

        // 步骤2：JD ∩ DLTB(建设用地)，拆分面积字段
        LayerInfo jdLayer = LayerInfoBuilder.fromDatasetEntity(jdDataset);
        LayerInfo dltbFilteredLayer = LayerInfoBuilder.fromAnalysisResult(step1Result);

        IntersectSplitParam splitParam = new IntersectSplitParam(
                List.of("jcmj"),
                List.of("tbmj", "kcmj", "tbdlmj")
        );

        AnalysisResult finalResult;
        try {
            finalResult = analysisExecutor.execute(
                    AnalysisType.INTERSECT_SPLIT,
                    List.of(jdLayer, dltbFilteredLayer),
                    splitParam,
                    schema
            );
        } finally {
            cleanupTable(schema, step1Result.getResultTableName());
        }
        log.info("[开发区-建设密度] 步骤2完成(相交拆分), 结果表={}, 要素数={}",
                finalResult.getResultTableName(), finalResult.getFeatureCount());

        return saveResultDataset(finalResult, "开发区建设密度", schema);
    }

    // ======================== 5-6. 要素名称 ========================

    @Override
    public Long analyzeElementName(Long kfqDatasetId, Long jdDatasetId) {
        log.info("[开发区-要素名称] 开始, kfqDatasetId={}, jdDatasetId={}", kfqDatasetId, jdDatasetId);

        String schema = datasetProperties.getSchema();
        DatasetEntity kfqDataset = getDataset(kfqDatasetId);
        DatasetEntity jdDataset = getDataset(jdDatasetId);

        // KFQ ∩ JD，拆分KFQ的jcmj字段
        LayerInfo kfqLayer = LayerInfoBuilder.fromDatasetEntity(kfqDataset);
        LayerInfo jdLayer = LayerInfoBuilder.fromDatasetEntity(jdDataset);

        IntersectSplitParam splitParam = new IntersectSplitParam(
                List.of("jcmj"),
                List.of()
        );

        AnalysisResult finalResult = analysisExecutor.execute(
                AnalysisType.INTERSECT_SPLIT,
                List.of(kfqLayer, jdLayer),
                splitParam,
                schema
        );
        log.info("[开发区-要素名称] 完成, 结果表={}, 要素数={}",
                finalResult.getResultTableName(), finalResult.getFeatureCount());

        return saveResultDataset(finalResult, "开发区要素名称", schema);
    }

    // ======================== 7-8. 变化情况 ========================

    @Override
    public Long analyzeChangeStatus(Long kfqDatasetId) {
        log.info("[开发区-变化情况] 开始, kfqDatasetId={}", kfqDatasetId);

        String schema = datasetProperties.getSchema();
        DatasetEntity kfqDataset = getDataset(kfqDatasetId);

        // 在KFQ上计算change_type字段
        LayerInfo kfqLayer = LayerInfoBuilder.fromDatasetEntity(kfqDataset);

        AttributeCalculateParam calcParam = new AttributeCalculateParam(
                List.of(new CalculatedField("change_type", buildChangeTypeExpression()))
        );

        AnalysisResult finalResult = analysisExecutor.execute(
                AnalysisType.ATTRIBUTE_CALCULATE,
                List.of(kfqLayer),
                calcParam,
                schema
        );
        log.info("[开发区-变化情况] 完成, 结果表={}, 要素数={}",
                finalResult.getResultTableName(), finalResult.getFeatureCount());

        return saveResultDataset(finalResult, "开发区变化情况", schema);
    }

    // ======================== 9-10. 土地利用现状 ========================

    @Override
    public Long analyzeLandUse(Long kfqDatasetId, Long dltbDatasetId, Caliber caliber) {
        log.info("[开发区-土地利用现状] 开始, kfqDatasetId={}, dltbDatasetId={}, caliber={}",
                kfqDatasetId, dltbDatasetId, caliber.getDesc());

        String schema = datasetProperties.getSchema();
        DatasetEntity kfqDataset = getDataset(kfqDatasetId);
        DatasetEntity dltbDataset = getDataset(dltbDatasetId);


        // KFQ ∩ DLTB，拆分面积字段
        LayerInfo kfqLayer = LayerInfoBuilder.fromDatasetEntity(kfqDataset);
        LayerInfo dltbLayer = LayerInfoBuilder.fromDatasetEntity(dltbDataset);

        IntersectSplitParam splitParam = new IntersectSplitParam(
                List.of("jcmj"),
                List.of("tbmj", "kcmj", "tbdlmj")
        );

        AnalysisResult finalResult = analysisExecutor.execute(
                AnalysisType.INTERSECT_SPLIT,
                List.of(kfqLayer, dltbLayer),
                splitParam,
                schema
        );
        log.info("[开发区-土地利用现状] 完成, 结果表={}, 要素数={}",
                finalResult.getResultTableName(), finalResult.getFeatureCount());

        return saveResultDataset(finalResult, "开发区土地利用现状(" + caliber.getDesc() + ")", schema);
    }

    // ======================== 辅助方法 ========================

    /**
     * 构建建设用地过滤WHERE子句
     */
    private String buildJsydFilterClause() {
        String values = BusinessConstants.JSYD_DLBM.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));
        return "dlbm IN (%s)".formatted(values);
    }

    /**
     * 构建变化情况change_type字段的CASE表达式
     * <p>
     * 根据JSZT2024和JSZT2025的组合，计算变化类型。
     */
    private String buildChangeTypeExpression() {
        return """
                CASE
                  WHEN jszt2024 IS NULL AND jszt2025 = 'YJC' THEN 'YJC_INCREASE'
                  WHEN jszt2024 IS NULL AND jszt2025 = 'WJS' THEN 'WJS_INCREASE'
                  WHEN jszt2024 IS NULL AND jszt2025 = 'ZZJS' THEN 'ZZJS_INCREASE'
                  WHEN jszt2024 = 'YJC' AND jszt2025 IS NULL THEN 'YJC_DECREASE'
                  WHEN jszt2024 = 'WJS' AND jszt2025 IS NULL THEN 'WJS_DECREASE'
                  WHEN jszt2024 = 'ZZJS' AND jszt2025 IS NULL THEN 'ZZJS_DECREASE'
                  WHEN jszt2024 = 'WJS' AND jszt2025 = 'ZZJS' THEN 'WJS_TO_ZZJS'
                  WHEN jszt2024 = 'WJS' AND jszt2025 = 'YJC' THEN 'WJS_TO_YJC'
                  WHEN jszt2024 = 'ZZJS' AND jszt2025 = 'YJC' THEN 'ZZJS_TO_YJC'
                  WHEN jszt2024 = 'ZZJS' AND jszt2025 = 'WJS' THEN 'ZZJS_TO_WJS'
                  WHEN jszt2024 = 'YJC' AND jszt2025 = 'ZZJS' THEN 'YJC_TO_ZZJS'
                  WHEN jszt2024 = 'YJC' AND jszt2025 = 'WJS' THEN 'YJC_TO_WJS'
                  WHEN jszt2024 = 'YJC' AND jszt2025 = 'YJC' THEN 'YJC_NO_CHANGE'
                  WHEN jszt2024 = 'WJS' AND jszt2025 = 'WJS' THEN 'WJS_NO_CHANGE'
                  WHEN jszt2024 = 'ZZJS' AND jszt2025 = 'ZZJS' THEN 'ZZJS_NO_CHANGE'
                  ELSE 'UNKNOWN'
                END""";
    }

    /**
     * 获取数据集，不存在则抛异常
     */
    private DatasetEntity getDataset(Long datasetId) {
        DatasetEntity dataset = datasetService.getById(datasetId);
        if (dataset == null) {
            throw new IllegalArgumentException("数据集不存在: " + datasetId);
        }
        return dataset;
    }

    /**
     * 清理中间结果表
     */
    private void cleanupTable(String schema, String tableName) {
        String fullTableName = TableNameUtils.getTableNameWithSchema(schema, tableName);
        geometryService.dropTableIfExists(fullTableName);
    }

    /**
     * 将分析结果保存为数据集
     */
    private Long saveResultDataset(AnalysisResult result, String name, String schema) {
        DatasetEntity entity = new DatasetEntity();
        entity.setDatasetName(name);
        entity.setDatasetType("analysis_result");
        entity.setSchemaName(schema);
        entity.setTableName(result.getResultTableName());
        entity.setLayerName(result.getResultLayerName());
        entity.setGeomType(result.getGeomType());
        entity.setSrid(result.getSrid());
        entity.setFeatureCount(result.getFeatureCount());
        entity.setStatus(UploadStatus.SUCCESS);
        entity.setMessage(result.getMessage());
        entity.setCreatedAt(Instant.now());
        datasetService.save(entity);
        return entity.getId();
    }

}
