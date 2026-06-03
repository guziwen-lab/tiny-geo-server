package com.supermap.analyze;

import com.supermap.modules.analyze.entity.DatasetEntity;
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
     * 要素数量
     */
    private long featureCount;

    /**
     * 执行耗时(ms)
     */
    private long cost;

    /**
     * 附加信息
     */
    private String message;

}
