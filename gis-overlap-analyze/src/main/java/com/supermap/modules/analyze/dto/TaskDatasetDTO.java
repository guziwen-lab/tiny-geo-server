package com.supermap.modules.analyze.dto;

import java.time.Instant;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图层引用表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "图层引用表")
@Data
public class TaskDatasetDTO extends PageParam {

	@Schema(title = "开始时间")
	private Instant startTime;

	@Schema(title = "结束时间")
	private Instant endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "任务ID")
	private Long taskId;

	@Schema(title = "数据集ID")
	private Long datasetId;

}
