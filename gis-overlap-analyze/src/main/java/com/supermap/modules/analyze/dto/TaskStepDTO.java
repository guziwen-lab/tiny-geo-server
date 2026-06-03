package com.supermap.modules.analyze.dto;

import java.time.Instant;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务执行记录表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "任务执行记录表")
@Data
public class TaskStepDTO extends PageParam {

	@Schema(title = "开始时间")
	private Instant startTime;

	@Schema(title = "结束时间")
	private Instant endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "任务ID")
	private Long taskId;

	@Schema(title = "步骤编号")
	private Integer stepNo;

	@Schema(title = "输入表")
	private String inputTable;

	@Schema(title = "叠加表")
	private String overlayTable;

	@Schema(title = "输出表")
	private String outputTable;

	@Schema(title = "状态")
	private String status;

}
