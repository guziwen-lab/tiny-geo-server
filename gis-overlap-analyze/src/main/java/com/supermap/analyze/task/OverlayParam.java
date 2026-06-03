package com.supermap.analyze.task;

import com.supermap.analyze.AnalysisParam;
import com.supermap.enumeration.OverlayType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OverlayParam implements AnalysisParam {

    private OverlayType overlayType;

}