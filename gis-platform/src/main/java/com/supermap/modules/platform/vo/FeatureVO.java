package com.supermap.modules.platform.vo;

import com.supermap.modules.platform.entity.FeatureEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * geo feature
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "geo feature")
@Data
public class FeatureVO extends FeatureEntity {

    @Schema(title = "GeoJSON")
    private String geoJson;

}
