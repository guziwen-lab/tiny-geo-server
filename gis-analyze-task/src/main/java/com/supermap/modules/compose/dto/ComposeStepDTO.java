package com.supermap.modules.compose.dto;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 组合任务任务关联表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "组合任务任务关联表")
@Data
public class ComposeStepDTO extends PageParam {

    @Schema(title = "开始时间")
    private Instant startTime;

    @Schema(title = "结束时间")
    private Instant endTime;

    @Schema(title = "主键")
    private Long id;

    @Schema(title = "组合任务id")
    private Long composeId;

    @Schema(title = "任务id")
    private Long taskId;

    @Schema(title = "排序")
    private Integer sort;

}
