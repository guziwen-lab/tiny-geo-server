package com.supermap.modules.business.dto;

import com.supermap.modules.business.enums.Caliber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "开发区土地利用现状请求")
public class KfqLandUseDTO {

    @NotNull
    @Schema(title = "KFQ开发区层数据集ID")
    private Long kfqDatasetId;

    @NotNull
    @Schema(title = "DLTB地类图斑数据集ID")
    private Long dltbDatasetId;

    @NotNull
    @Schema(title = "口径（NON_TONG_KOU_JING/TONG_KOU_JING）")
    private Caliber caliber;

}