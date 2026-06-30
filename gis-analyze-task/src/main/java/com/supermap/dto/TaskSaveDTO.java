package com.supermap.dto;

import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 任务表
 *
 * @author gzw
 */
@Schema(title = "任务表")
@Data
public class TaskSaveDTO {

    @NotBlank
    @Schema(title = "任务名称")
    private String taskName;

    @NotNull
    @Schema(title = "分析类型")
    private AnalysisType analysisType;

    @Schema(title = "子类型")
    private String subType;

    @Schema(title = "几何类型")
    private GeomType geomType;

    @Schema(title = "任务数据集列表")
    @NotEmpty
    private List<TaskDatasetSaveDTO> datasetIds;

}
