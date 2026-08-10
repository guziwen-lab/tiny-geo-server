package com.supermap.task.support.analysis;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisParam;
import com.supermap.analyze.LayerInfo;
import com.supermap.gdal.config.GdalProperties;
import com.supermap.task.config.TaskConfigurationProperties;
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
    private final GdalProperties gdalProperties;

    public <T extends AnalysisParam> AnalysisContext<T> buildAnalysisContext(List<LayerInfo> layers,
                                                                             T param,
                                                                             String resultTableName) {
        AnalysisContext<T> context = new AnalysisContext<>();
        context.setInputLayers(layers);
        context.setSchema(gdalProperties.getSchema());
        context.setResultTableName(resultTableName);
        context.setParam(param);
        context.setPkCol(taskConfigurationProperties.getPkColumnName());
        return context;
    }

}
