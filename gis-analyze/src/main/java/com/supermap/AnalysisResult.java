package com.supermap;

import com.supermap.enums.GeomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gzw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    /**
     * 分析任务ID
     */
    private Long taskId;

    /**
     * 结果表名
     */
    private String resultTableName;

    /**
     * 输出图层名称
     */
    private String resultLayerName;

    /**
     * 要素数量
     */
    private long featureCount;

    /**
     * SRID
     */
    private Integer srid;

    /**
     * Geometry 类型
     */
    private GeomType geomType;

    /**
     * 执行耗时(ms)
     */
    private long cost;

    /**
     * 附加信息
     */
    private String message;

}
