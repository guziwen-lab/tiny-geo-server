package com.supermap.modules.dataset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 一批 GDB 图层导入请求。
 * 同一图层、同一 SRID、同一几何类型的数据会追加到同一个数据集。
 */
@Data
@Schema(title = "批量导入GDB")
public class BatchImportGdbDTO {

    @NotEmpty
    @Schema(title = "GDB 路径列表")
    private String paths;

    @NotEmpty
    @Schema(title = "指定图层名；为空时导入每个 GDB 中的全部图层")
    private String layerName;

}
