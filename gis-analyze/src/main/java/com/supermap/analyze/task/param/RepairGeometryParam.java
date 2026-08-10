package com.supermap.analyze.task.param;

import com.supermap.analyze.AnalysisParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gzw
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepairGeometryParam implements AnalysisParam {

    private Integer srid;

}
