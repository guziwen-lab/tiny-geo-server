package com.supermap.modules.analyze.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

/**
 * 数据集表
 *
 * @author gzw
 */
@Schema(title = "数据集表")
@Data
public class DatasetSaveDTO {

	@NotNull(groups = Update.class)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "数据集名称")
	private String datasetName;

	@Schema(title = "数据集类型")
	private String datasetType;

	@Schema(title = "源文件")
	private Long sourceFile;

	@Schema(title = "表名")
	private String tableName;

	@Schema(title = "几何类型")
	private String geomType;

	@Schema(title = "空间参考系统")
	private Integer srid;

	@Schema(title = "特征数量")
	private Long featureCount;

	@Schema(title = "创建时间")
	private Instant createdAt;

}
