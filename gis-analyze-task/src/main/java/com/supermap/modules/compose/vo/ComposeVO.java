package com.supermap.modules.compose.vo;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组合任务表
 *
 * @author gzw
 */
@Schema(title = "组合任务表")
@Data
public class ComposeVO<T extends AnalysisParam> {

    private TaskEntity taskEntity;

    private AnalysisContext<T> analysisContext;

}
