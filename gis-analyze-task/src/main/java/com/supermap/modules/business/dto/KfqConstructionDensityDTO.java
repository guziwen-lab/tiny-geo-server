package com.supermap.modules.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "开发区建设密度请求")
public class KfqConstructionDensityDTO {

    @NotNull
    @Schema(title = "JD基层数据集ID")
    private Long jdDatasetId;

    @NotNull
    @Schema(title = "DLTB地类图斑数据集ID")
    private Long dltbDatasetId;

}