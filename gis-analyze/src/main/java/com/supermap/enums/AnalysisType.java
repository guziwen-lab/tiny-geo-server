package com.supermap.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gzw
 */
@Getter
@AllArgsConstructor
public enum AnalysisType {

    OVERLAY("图层叠加"),
    INTERSECT_SPLIT("相交面积拆分"),
    BUFFER("缓冲区分析"),
    DISSOLVE("消减"),
    SPATIAL_JOIN("空间连接"),
    FILTER("属性过滤"),
    ATTRIBUTE_CALCULATE("属性计算");

    private final String desc;

}
