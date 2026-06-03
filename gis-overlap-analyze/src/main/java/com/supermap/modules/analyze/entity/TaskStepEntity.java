package com.supermap.modules.analyze.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务执行记录表
 *
 * @author gzw
 */
@Schema(title = "任务执行记录表")
@Data
@TableName("gis_task_step")
public class TaskStepEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
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

}
