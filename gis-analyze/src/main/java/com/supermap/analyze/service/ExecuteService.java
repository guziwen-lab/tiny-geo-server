package com.supermap.analyze.service;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisParam;
import com.supermap.analyze.LayerInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author gzw
 */
public interface ExecuteService<T extends AnalysisParam> {

    LayerInfo execute(@NotNull LayerInfo current,
                      @Nullable LayerInfo next,
                      @NotBlank String resultTableName,
                      @NotNull AnalysisContext<T> context);

    LayerInfo execute(@NotNull LayerInfo current, @Nullable LayerInfo next, @NotNull AnalysisContext<T> context);

}
