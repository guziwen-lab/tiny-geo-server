package com.supermap.modules.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "开发区要素名称请求")
public class KfqElementNameDTO {

    @NotNull
    @Schema(title = "KFQ开发区层数据集ID")
    private Long kfqDatasetId;

    @NotNull
    @Schema(title = "JD基层数据集ID")
    private Long jdDatasetId;

}