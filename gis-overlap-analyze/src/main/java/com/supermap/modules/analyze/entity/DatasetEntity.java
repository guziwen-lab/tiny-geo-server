package com.supermap.modules.analyze.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.supermap.enumeration.GeomType;
import com.supermap.enumeration.UploadStatus;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据集表
 *
 * @author gzw
 */
@Schema(title = "数据集表")
@Data
@TableName("gis_dataset")
public class DatasetEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "数据集名称")
    private String datasetName;

    @Schema(title = "数据集类型")
    private String datasetType;

    @Schema(title = "源文件")
    private String sourceFile;

    @Schema(title = "图层名")
    private String layerName;

    @Schema(title = "表名")
    private String tableName;

    @Schema(title = "几何类型")
    private GeomType geomType;

    @Schema(title = "空间参考系统")
    private Integer srid;

    @Schema(title = "要素数量")
    private Long featureCount;

    @Schema(title = "无效要素数量")
    private Long invalidFeatureCount;

    @Schema(title = "上传状态")
    private UploadStatus status;

    @Schema(title = "附加信息")
    private String message;

    @Schema(title = "创建时间")
    private Instant createdAt;

}
