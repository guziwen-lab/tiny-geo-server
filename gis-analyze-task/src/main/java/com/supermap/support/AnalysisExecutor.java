package com.supermap.support;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisEngine;
import com.supermap.AnalysisParam;
import com.supermap.AnalysisResult;
import com.supermap.LayerInfo;
import com.supermap.enums.AnalysisType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分析执行器
 * <p>
 * 封装同步执行分析引擎的逻辑，供业务编排服务调用。
 * 负责构建分析上下文、生成唯一结果表名、调用分析引擎并返回结果。
 *
 * @author gzw
 */
@Component
@RequiredArgsConstructor
public class AnalysisExecutor {

    private final AnalysisEngine analysisEngine;

    private static final AtomicLong COUNTER = new AtomicLong(0);

    /**
     * 同步执行分析任务
     *
     * @param type   分析类型
     * @param layers 输入图层列表
     * @param param  分析参数
     * @param schema 数据库 schema
     * @return 分析结果
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AnalysisResult execute(AnalysisType type, List<LayerInfo> layers, AnalysisParam param, String schema) {
        AnalysisContext context = new AnalysisContext();
        context.setInputLayers(layers);
        context.setSchema(schema);
        context.setResultTableName(generateResultTableName());
        context.setResultLayerName(context.getResultTableName());
        context.setParam(param);

        return analysisEngine.execute(type, context);
    }

    /**
     * 生成唯一的结果表名
     * <p>
     * 使用 analyze_biz_ 前缀，符合 SqlInjectionCheck 的表名校验规则。
     */
    private String generateResultTableName() {
        return "analyze_biz_" + System.currentTimeMillis() + "_" + COUNTER.getAndIncrement();
    }

}
