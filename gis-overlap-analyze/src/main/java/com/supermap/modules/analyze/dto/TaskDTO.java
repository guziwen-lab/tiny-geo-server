package com.supermap.modules.analyze.dto;

import java.time.Instant;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "任务表")
@Data
public class TaskDTO extends PageParam {

	@Schema(title = "开始时间")
	private Instant startTime;

	@Schema(title = "结束时间")
	private Instant endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "任务名称")
	private String taskName;

	@Schema(title = "任务类型")
	private String taskType;

	@Schema(title = "状态")
	private String status;

	@Schema(title = "创建时间")
	private Instant createdAt;

	@Schema(title = "完成时间")
	private Instant finishedAt;

}
