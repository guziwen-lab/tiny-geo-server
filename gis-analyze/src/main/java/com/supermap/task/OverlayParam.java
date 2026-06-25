package com.supermap.task;

import com.supermap.AnalysisParam;
import com.supermap.enumeration.OverlayAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OverlayParam implements AnalysisParam {

    private OverlayAlgorithm overlayAlgorithm;

}