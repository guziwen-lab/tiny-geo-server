package com.supermap;

import com.supermap.enums.GeomType;
import com.supermap.type.Column;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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

    /**
     * 当前分析过程中图层的元数据。
     * columns 表示当前 tableName 对应表的属性字段（不包含 geom）。
     */
    @Schema(title = "字段列表")
    private List<Column> columns;

}
