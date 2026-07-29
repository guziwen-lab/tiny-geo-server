package com.supermap.modules.business.dto;

import com.supermap.modules.business.enums.Caliber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "其他农用地分析请求")
public class QtnydbhAnalyzeDTO {

    @NotNull
    @Schema(title = "ZT监测图层数据集ID")
    private Long ztDatasetId;

    @NotNull
    @Schema(title = "DLTB地类图斑数据集ID")
    private Long dltbDatasetId;

    @NotNull
    @Schema(title = "口径（NON_TONG_KOU_JING/TONG_KOU_JING）")
    private Caliber caliber;

}