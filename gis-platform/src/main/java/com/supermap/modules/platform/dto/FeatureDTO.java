package com.supermap.modules.platform.dto;

import com.supermap.dto.SearchDTO;
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
public class FeatureDTO extends SearchDTO {

}
