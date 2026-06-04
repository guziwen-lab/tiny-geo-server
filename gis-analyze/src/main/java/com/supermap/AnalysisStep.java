package com.supermap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分析步骤记录（分析引擎层DTO，不依赖持久化实体）
 *
 * @author gzw
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisStep {

    /**
     * 步骤编号
     */
    private int stepNo = 1;

    /**
     * 输入表
     */
    private String inputTable;

    /**
     * 叠加表
     */
    private String overlayTable;

    /**
     * 输出表
     */
    private String outputTable;

}
