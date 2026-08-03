package com.supermap.modules.compose.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 组合任务任务关联表
 *
 * @author gzw
 */
@Schema(title = "组合任务任务关联表")
@Data
public class ComposeStepSaveDTO {

    @NotNull(groups = Update.class)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "组合任务id")
    private Long composeId;

    @Schema(title = "任务id")
    private Long taskId;

    @Schema(title = "排序")
    private Integer sort;

}
