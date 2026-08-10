package com.supermap.analyze.service;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisParam;
import com.supermap.analyze.LayerInfo;

/**
 * @author gzw
 */
public interface ExecuteService<T extends AnalysisParam> {

    LayerInfo execute(LayerInfo current, LayerInfo next, AnalysisContext<T> context);

}
