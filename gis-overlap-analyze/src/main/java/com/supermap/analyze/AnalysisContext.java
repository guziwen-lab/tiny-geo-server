package com.supermap.analyze;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Data
public class AnalysisContext<T extends AnalysisParam> {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 输入图层
     */
    private List<LayerInfo> inputLayers;

    /**
     * 结果图层名称
     */
    private String resultLayerName;

    /**
     * 结果表名
     */
    private String resultTableName;

    /**
     * 是否清理中间表
     */
    private boolean isCleanTemp = false;

    /**
     * 参数
     */
    private T param;

    /**
     * 分析步骤记录
     */
    private List<AnalysisStep> steps = new ArrayList<>();

    /**
     * 记录一个分析步骤
     */
    public void addStep(AnalysisStep step) {
        this.steps.add(step);
    }

}
