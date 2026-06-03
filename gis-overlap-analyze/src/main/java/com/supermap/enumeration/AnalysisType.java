package com.supermap.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gzw
 */
@Getter
@AllArgsConstructor
public enum AnalysisType {

    OVERLAY("图层叠加"),
    BUFFER("缓冲区分析"),
    DISSOLVE("消减"),
    SPATIAL_JOIN("空间连接");

    private final String desc;

}
