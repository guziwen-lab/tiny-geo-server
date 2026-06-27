package com.supermap.resolver;

import com.supermap.common.util.CollectionUtils;
import com.supermap.enumeration.AnalysisType;
import com.supermap.enumeration.GeomType;
import com.supermap.modules.analyze.entity.DatasetEntity;

import java.util.List;

public final class GeomTypeResolver {

    private GeomTypeResolver() {
    }

    public static GeomType resolve(AnalysisType analysisType,
                                   List<DatasetEntity> datasets) {
        if (CollectionUtils.isEmpty(datasets)) {
            throw new IllegalArgumentException("数据集不能为空");
        }

        return switch (analysisType) {
            // Overlay 所有算法共用同一套推导规则
            case OVERLAY -> resolveOverlay(datasets);

            // Buffer 永远输出面
            case BUFFER -> GeomType.MULTI_POLYGON;

            // Dissolve 保持输入类型
            case DISSOLVE -> datasets.get(0).getGeomType();

            // Spatial Join 保持目标图层类型（默认第一个图层）
            case SPATIAL_JOIN -> datasets.get(0).getGeomType();
        };
    }

    /**
     * Overlay 几何类型推导
     */

    private static GeomType resolveOverlay(List<DatasetEntity> datasets) {
        GeomType result = datasets.get(0).getGeomType();
        for (int i = 1; i < datasets.size(); i++) {
            result = resolveOverlay(result, datasets.get(i).getGeomType());
        }
        return result;
    }

    /**
     * Overlay 两两推导
     */
    private static GeomType resolveOverlay(GeomType left, GeomType right) {
        // Point 与任何图层叠加，结果最多为 Point
        if (left == GeomType.POINT || right == GeomType.POINT) {
            return GeomType.POINT;
        }

        // Line 与 Polygon 叠加，结果为 Line
        if ((left == GeomType.MULTI_LINE_STRING && right == GeomType.MULTI_POLYGON)
                || (left == GeomType.MULTI_POLYGON && right == GeomType.MULTI_LINE_STRING)) {
            return GeomType.MULTI_LINE_STRING;
        }

        // Line 与 Line
        if (left == GeomType.MULTI_LINE_STRING && right == GeomType.MULTI_LINE_STRING) {
            return GeomType.MULTI_LINE_STRING;
        }

        // Polygon 与 Polygon
        if (left == GeomType.MULTI_POLYGON && right == GeomType.MULTI_POLYGON) {
            return GeomType.MULTI_POLYGON;
        }

        throw new IllegalArgumentException("不支持的几何类型组合：" + left + " + " + right);
    }

}