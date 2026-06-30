package com.supermap.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务执行记录表
 *
 * @author gzw
 */
@Schema(title = "任务执行记录表")
@Data
public class TaskStepSaveDTO {

	@NotNull(groups = Update.class)
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
