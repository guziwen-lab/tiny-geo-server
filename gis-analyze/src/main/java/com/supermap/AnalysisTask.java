package com.supermap;

import com.supermap.enums.AnalysisType;

public interface AnalysisTask<T extends AnalysisParam> {

    AnalysisType getType();

    T buildParam(String subType);

    AnalysisResult execute(AnalysisContext<T> context);

}