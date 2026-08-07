package com.supermap.support;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.LayerInfo;
import com.supermap.config.TaskConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AnalysisContext 构建工具
 *
 * @author gzw
 */
@Component
@RequiredArgsConstructor
public final class AnalysisContextBuilder {

    private final TaskConfigurationProperties taskConfigurationProperties;

    public <T extends AnalysisParam> AnalysisContext<T> buildAnalysisContext(List<LayerInfo> layers,
                                                                             T param,
                                                                             String schema,
                                                                             String resultTableName) {
        AnalysisContext<T> context = new AnalysisContext<>();
        context.setInputLayers(layers);
        context.setSchema(schema);
        context.setResultTableName(resultTableName);
        context.setParam(param);
        context.setPkCol(taskConfigurationProperties.getPkColumnName());
        return context;
    }

}
