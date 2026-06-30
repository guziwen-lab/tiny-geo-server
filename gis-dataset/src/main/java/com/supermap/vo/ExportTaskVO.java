package com.supermap.vo;

import com.supermap.entity.ExportTaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${comments}
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "${comments}")
@Data
public class ExportTaskVO extends ExportTaskEntity {

}
