package com.supermap;

import com.supermap.enumeration.GeomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author gzw
 */
@Data
public class LayerInfo {

    @Schema(title = "表名")
    private String tableName;

    @Schema(title = "几何类型")
    private GeomType geomType;

    @Schema(title = "空间参考系统")
    private Integer srid;

}
