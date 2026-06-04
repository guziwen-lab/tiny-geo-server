package com.supermap.task;

import com.supermap.AnalysisParam;
import com.supermap.enumeration.OverlayType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OverlayParam implements AnalysisParam {

    private OverlayType overlayType;

}