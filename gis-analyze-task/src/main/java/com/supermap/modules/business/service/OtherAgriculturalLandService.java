package com.supermap.modules.business.service;

import com.supermap.modules.business.enums.Caliber;

/**
 * 其他农用地分析服务
 * <p>
 * 2025年自然资源监测-其他农用地图斑分析。
 * 将ZT监测图层与DLTB地类图斑相交并按面积拆分，
 * 然后按口径过滤提取其他农用地变化图斑。
 *
 * @author gzw
 */
public interface OtherAgriculturalLandService {

    /**
     * 执行其他农用地分析
     *
     * @param ztDatasetId  ZT监测图层数据集ID
     * @param dltbDatasetId DLTB地类图斑数据集ID
     * @param caliber       口径（非同口径/同口径）
     * @return 结果数据集ID
     */
    Long analyze(Long ztDatasetId, Long dltbDatasetId, Caliber caliber);

}
