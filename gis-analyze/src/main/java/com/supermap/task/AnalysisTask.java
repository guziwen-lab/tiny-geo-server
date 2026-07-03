package com.supermap.task;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.AnalysisResult;
import com.supermap.enums.AnalysisType;

public interface AnalysisTask<T extends AnalysisParam> {

    AnalysisType getType();

    T buildParam(String subType);

    AnalysisResult execute(AnalysisContext<T> context);

}