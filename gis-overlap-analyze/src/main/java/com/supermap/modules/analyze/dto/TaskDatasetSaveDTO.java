package com.supermap.modules.analyze.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

/**
 * 图层引用表
 *
 * @author gzw
 */
@Schema(title = "图层引用表")
@Data
public class TaskDatasetSaveDTO {

    @NotNull
    @Schema(title = "数据集ID")
    private Long datasetId;

}
