package com.supermap.modules.compose.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.supermap.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组合任务步骤表
 *
 * @author gzw
 */
@Schema(title = "组合任务步骤表")
@Data
@TableName("gis_compose_step")
public class ComposeStepEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "组合任务id")
	private Long composeId;

	@Schema(title = "步骤信息")
	private String description;

	@Schema(title = "附加信息")
	private String message;

	@Schema(title = "状态")
	private TaskStatus status;

	@Schema(title = "排序")
	private Integer sort;

}
