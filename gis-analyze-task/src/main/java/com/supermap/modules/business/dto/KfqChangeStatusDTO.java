package com.supermap.modules.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "开发区变化情况请求")
public class KfqChangeStatusDTO {

    @NotNull
    @Schema(title = "KFQ开发区层数据集ID")
    private Long kfqDatasetId;

}