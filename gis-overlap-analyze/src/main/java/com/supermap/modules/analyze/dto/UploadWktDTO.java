package com.supermap.modules.analyze.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * @author gzw
 */
@Schema(title = "上传wkt")
@Data
public class UploadWktDTO {

    @NotBlank
    @Schema(title = "name")
    private String name;

    @Schema(title = "properties")
    private Map<String, Object> properties;

    @NotBlank
    @Schema(title = "wkt")
    private String wkt;

    @NotNull
    @Schema(title = "SRID")
    private Integer srid;

}
