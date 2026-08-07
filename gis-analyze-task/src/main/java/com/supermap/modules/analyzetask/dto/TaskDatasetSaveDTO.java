package com.supermap.modules.analyzetask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图层引用表
 *
 * @author gzw
 */
@Schema(title = "图层引用表")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDatasetSaveDTO {

    @NotNull
    @Schema(title = "数据集ID")
    private Long datasetId;

}
