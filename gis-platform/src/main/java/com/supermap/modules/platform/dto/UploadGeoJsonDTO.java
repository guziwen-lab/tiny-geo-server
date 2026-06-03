package com.supermap.modules.platform.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author gzw
 */
@Schema(title = "上传geojson")
@Data
public class UploadGeoJsonDTO {

    @NotBlank
    @Schema(title = "name")
    private String name;

    @NotNull
    @Schema(title = "geoJson")
    private JsonNode geoJson;

}
