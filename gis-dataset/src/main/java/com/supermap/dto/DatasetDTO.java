package com.supermap.dto;

import java.time.Instant;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据集表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "数据集表")
@Data
public class DatasetDTO extends PageParam {

	@Schema(title = "开始时间")
	private Instant startTime;

	@Schema(title = "结束时间")
	private Instant endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "数据集名称")
	private String datasetName;

	@Schema(title = "数据集类型")
	private String datasetType;

	@Schema(title = "源文件")
	private String sourceFile;

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
