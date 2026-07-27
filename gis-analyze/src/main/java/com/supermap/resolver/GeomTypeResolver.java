package com.supermap.resolver;

import com.supermap.LayerInfo;
import com.supermap.common.util.CollectionUtils;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;

import java.util.List;

public final class GeomTypeResolver {

    private GeomTypeResolver() {
    }

    public static GeomType resolve(AnalysisType analysisType, List<LayerInfo> layerInfos) {
        if (CollectionUtils.isEmpty(layerInfos)) {
            throw new IllegalArgumentException("数据集不能为空");
        }

        return switch (analysisType) {
            // Overlay 所有算法共用同一套推导规则
            case OVERLAY -> resolveOverlay(layerInfos);

            // Buffer 永远输出面
            case BUFFER -> GeomType.MULTI_POLYGON;

            // Dissolve 保持输入类型
            case DISSOLVE -> layerInfos.get(0).getGeomType();

            // Spatial Join 保持目标图层类型（默认第一个图层）
            case SPATIAL_JOIN -> layerInfos.get(0).getGeomType();
        };
    }

    /**
     * Overlay 几何类型推导
     */
    private static GeomType resolveOverlay(List<LayerInfo> layerInfos) {
        GeomType result = layerInfos.get(0).getGeomType();
        for (int i = 1; i < layerInfos.size(); i++) {
            result = resolveOverlay(result, layerInfos.get(i).getGeomType());
        }
        return result;
    }

    private static GeomType resolveOverlay(GeomType left, GeomType right) {
        return left.getCollectionExtractType() <= right.getCollectionExtractType() ? left : right;
    }

}