package com.supermap.task;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.AnalysisResult;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;

public interface AnalysisTask<T extends AnalysisParam> {

    /**
     * 分析类型
     *
     * @return 分析类型
     */
    AnalysisType getType();

    /**
     * 结果geom类型
     *
     * @param context 分析上下文
     * @return 几何类型
     */
    GeomType resultGeomType(AnalysisContext<T> context);

    /**
     * 构建分析参数
     *
     * @param param 构建参数
     * @return 分析参数
     */
    T buildParam(String param);

    /**
     * 执行分析
     *
     * @param context 分析上下文
     * @return 分析结果
     */
    AnalysisResult execute(AnalysisContext<T> context);

}