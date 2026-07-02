package com.supermap.service;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.LayerInfo;

/**
 * @author gzw
 */
public interface ExecuteService<T extends AnalysisParam> {

    LayerInfo execute(LayerInfo current, LayerInfo next, AnalysisContext<T> context);

}
