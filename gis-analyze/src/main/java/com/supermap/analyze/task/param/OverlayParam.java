package com.supermap.analyze.task.param;

import com.supermap.analyze.AnalysisParam;
import com.supermap.analyze.enums.OverlayAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OverlayParam implements AnalysisParam {

    private OverlayAlgorithm overlayAlgorithm;

}