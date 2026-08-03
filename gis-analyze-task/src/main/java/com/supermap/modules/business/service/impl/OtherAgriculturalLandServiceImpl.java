package com.supermap.modules.business.service.impl;

import com.supermap.AnalysisResult;
import com.supermap.LayerInfo;
import com.supermap.config.DatasetProperties;
import com.supermap.enums.UploadStatus;
import com.supermap.modules.business.constant.BusinessConstants;
import com.supermap.modules.business.enums.Caliber;
import com.supermap.modules.business.service.OtherAgriculturalLandService;
import com.supermap.support.AnalysisExecutor;
import com.supermap.support.LayerInfoBuilder;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.service.GeometryService;
import com.supermap.task.param.FilterParam;
import com.supermap.task.param.IntersectSplitParam;
import com.supermap.task.param.IntersectSplitParam.SplitField;
import com.supermap.util.TableNameUtils;
import com.supermap.enums.AnalysisType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 其他农用地分析服务实现
 * <p>
 * 工作流程：
 * <ol>
 *   <li>相交+面积拆分：ZT ∩ DLTB，拆分A表jcmj字段，B表tbmj/kcmj/tbdlmj字段</li>
 *   <li>过滤提取：根据口径过滤（非同口径用dlbm，同口径用dlbmtkj），
 *       同时过滤ysdm和tbdlmj_split面积阈值</li>
 *   <li>保存结果为数据集</li>
 * </ol>
 *
 * @author gzw
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtherAgriculturalLandServiceImpl implements OtherAgriculturalLandService {

    private final AnalysisExecutor analysisExecutor;
    private final DatasetService datasetService;
    private final DatasetProperties datasetProperties;
    private final GeometryService geometryService;

    @Override
    public Long analyze(Long ztDatasetId, Long dltbDatasetId, Caliber caliber) {
        log.info("[其他农用地分析] 开始, ztDatasetId={}, dltbDatasetId={}, caliber={}",
                ztDatasetId, dltbDatasetId, caliber.getDesc());

        String schema = datasetProperties.getSchema();

        // 1. 获取数据集
        DatasetEntity ztDataset = datasetService.getById(ztDatasetId);
        if (ztDataset == null) {
            throw new IllegalArgumentException("ZT数据集不存在: " + ztDatasetId);
        }
        DatasetEntity dltbDataset = datasetService.getById(dltbDatasetId);
        if (dltbDataset == null) {
            throw new IllegalArgumentException("DLTB数据集不存在: " + dltbDatasetId);
        }

        // 2. 构建图层信息
        LayerInfo ztLayer = LayerInfoBuilder.fromDatasetEntity(ztDataset);
        LayerInfo dltbLayer = LayerInfoBuilder.fromDatasetEntity(dltbDataset);

        // 3. 步骤1：相交+面积拆分
        IntersectSplitParam splitParam = new IntersectSplitParam(
                List.of(SplitField.withDefaultResult("jcmj")),
                List.of(SplitField.withDefaultResult("tbmj"), SplitField.withDefaultResult("kcmj"),
                        SplitField.withDefaultResult("tbdlmj")),
                "ZT_RATIO",
                "DLTB_RATIO"
        );
        AnalysisResult step1Result = analysisExecutor.execute(
                AnalysisType.INTERSECT_SPLIT,
                List.of(ztLayer, dltbLayer),
                splitParam,
                schema
        );
        log.info("[其他农用地分析] 步骤1完成(相交拆分), 结果表={}, 要素数={}",
                step1Result.getResultTableName(), step1Result.getFeatureCount());

        // 4. 步骤2：过滤提取
        String whereClause = buildWhereClause(caliber);
        FilterParam filterParam = new FilterParam(whereClause);

        LayerInfo step1Layer = LayerInfoBuilder.fromAnalysisResult(step1Result);

        AnalysisResult finalResult;
        try {
            finalResult = analysisExecutor.execute(
                    AnalysisType.FILTER,
                    List.of(step1Layer),
                    filterParam,
                    schema
            );
        } finally {
            // 清理步骤1的中间结果表
            String step1Table = TableNameUtils.getTableNameWithSchema(schema, step1Result.getResultTableName());
            geometryService.dropTableIfExists(step1Table);
        }
        log.info("[其他农用地分析] 步骤2完成(过滤提取), 结果表={}, 要素数={}",
                finalResult.getResultTableName(), finalResult.getFeatureCount());

        // 5. 保存结果为数据集
        Long datasetId = saveResultDataset(finalResult, caliber, schema);
        log.info("[其他农用地分析] 完成, 结果数据集ID={}", datasetId);

        return datasetId;
    }

    /**
     * 构建过滤WHERE子句
     * <p>
     * 非同口径：dlbm IN (...) AND ysdm IN (...) AND tbdlmj_split > 阈值
     * 同口径：dlbmtkj IN (...) AND ysdm IN (...) AND tbdlmj_split > 阈值
     */
    private String buildWhereClause(Caliber caliber) {
        String dlbmField = caliber == Caliber.TONG_KOU_JING ? "dlbmtkj" : "dlbm";

        String dlbmValues = BusinessConstants.QTYD_DLBM.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));
        String ysdmValues = BusinessConstants.QTYD_YSDM.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));

        return "%s IN (%s) AND ysdm IN (%s) AND tbdlmj_split > %s".formatted(
                dlbmField, dlbmValues, ysdmValues, BusinessConstants.AREA_THRESHOLD);
    }

    /**
     * 将分析结果保存为数据集
     */
    private Long saveResultDataset(AnalysisResult result, Caliber caliber, String schema) {
        DatasetEntity entity = new DatasetEntity();
        entity.setDatasetName("其他农用地变化图斑(" + caliber.getDesc() + ")");
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
