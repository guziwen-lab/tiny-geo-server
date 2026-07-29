package com.supermap.modules.business.support;

import com.supermap.AnalysisResult;
import com.supermap.LayerInfo;
import com.supermap.modules.dataset.entity.DatasetEntity;

/**
 * LayerInfo 构建工具
 * <p>
 * 从 DatasetEntity 或 AnalysisResult 构建 LayerInfo，
 * 供业务编排服务在多步分析中传递图层信息。
 *
 * @author gzw
 */
public final class LayerInfoBuilder {

    private LayerInfoBuilder() {
    }

    /**
     * 从数据集实体构建 LayerInfo
     *
     * @param dataset 数据集实体
     * @return 图层信息
     */
    public static LayerInfo fromDatasetEntity(DatasetEntity dataset) {
        LayerInfo layerInfo = new LayerInfo();
        layerInfo.setOriginalTableName(dataset.getTableName());
        layerInfo.setTableName(dataset.getTableName());
        layerInfo.setGeomType(dataset.getGeomType());
        layerInfo.setSrid(dataset.getSrid());
        return layerInfo;
    }

    /**
     * 从分析结果构建 LayerInfo（用于多步分析中传递中间结果）
     *
     * @param result 上一步分析结果
     * @return 图层信息
     */
    public static LayerInfo fromAnalysisResult(AnalysisResult result) {
        LayerInfo layerInfo = new LayerInfo();
        layerInfo.setOriginalTableName(result.getResultTableName());
        layerInfo.setTableName(result.getResultTableName());
        layerInfo.setGeomType(result.getGeomType());
        layerInfo.setSrid(result.getSrid());
        return layerInfo;
    }

}
