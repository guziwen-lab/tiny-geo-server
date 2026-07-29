package com.supermap.modules.business.service;

import com.supermap.modules.business.enums.Caliber;

/**
 * 国家级开发区分析服务
 * <p>
 * 2025年国家级开发区监测分析，输出10类矢量成果。
 *
 * @author gzw
 */
public interface DevelopmentZoneService {

    /**
     * 1-2. 建设状态输出
     * <p>
     * KFQ数据集直接作为结果（已有JSZT2025等字段），无需分析。
     *
     * @param kfqDatasetId KFQ开发区层数据集ID
     * @return 结果数据集ID（即输入的KFQ数据集ID）
     */
    Long exportConstructionStatus(Long kfqDatasetId);

    /**
     * 3-4. 建设密度分析
     * <p>
     * 先过滤DLTB为建设用地，再与JD相交拆分面积字段。
     *
     * @param jdDatasetId  JD基层数据集ID
     * @param dltbDatasetId DLTB地类图斑数据集ID
     * @return 结果数据集ID
     */
    Long analyzeConstructionDensity(Long jdDatasetId, Long dltbDatasetId);

    /**
     * 5-6. 要素名称分析
     * <p>
     * KFQ ∩ JD，拆分KFQ的jcmj字段。
     *
     * @param kfqDatasetId KFQ开发区层数据集ID
     * @param jdDatasetId  JD基层数据集ID
     * @return 结果数据集ID
     */
    Long analyzeElementName(Long kfqDatasetId, Long jdDatasetId);

    /**
     * 7-8. 变化情况分析
     * <p>
     * 在KFQ上根据JSZT2024/JSZT2025计算change_type字段。
     *
     * @param kfqDatasetId KFQ开发区层数据集ID
     * @return 结果数据集ID
     */
    Long analyzeChangeStatus(Long kfqDatasetId);

    /**
     * 9-10. 土地利用现状分析
     * <p>
     * KFQ ∩ DLTB，拆分面积字段。
     *
     * @param kfqDatasetId  KFQ开发区层数据集ID
     * @param dltbDatasetId DLTB地类图斑数据集ID
     * @param caliber        口径（非同口径/同口径）
     * @return 结果数据集ID
     */
    Long analyzeLandUse(Long kfqDatasetId, Long dltbDatasetId, Caliber caliber);

}
