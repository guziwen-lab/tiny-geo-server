package com.supermap.modules.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author gzw
 */
@Data
@Schema(title = "Bbox查询参数")
public class BboxQueryDTO {

    @NotNull
    private Double minX;

    @NotNull
    private Double minY;

    @NotNull
    private Double maxX;

    @NotNull
    private Double maxY;

}