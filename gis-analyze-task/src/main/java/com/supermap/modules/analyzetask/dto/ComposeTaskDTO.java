package com.supermap.modules.analyzetask.dto;

import com.supermap.AnalysisParam;
import com.supermap.enums.AnalysisType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Data
public class ComposeTaskDTO<T extends AnalysisParam> {

    private String taskName;

    private AnalysisType analysisType;

    private T taskParam;

    @Schema(title = "任务数据集列表")
    @NotEmpty
    private List<TaskDatasetSaveDTO> datasetIds = new ArrayList<>();

    public void addDataset(List<Long> datasetIdList) {
        for (Long datasetId : datasetIdList) {
            datasetIds.add(new TaskDatasetSaveDTO(datasetId));
        }
    }

}
