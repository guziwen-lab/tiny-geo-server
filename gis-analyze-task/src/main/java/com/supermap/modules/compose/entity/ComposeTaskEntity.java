package com.supermap.modules.compose.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组合任务任务关联表
 *
 * @author gzw
 */
@Schema(title = "组合任务任务关联表")
@Data
@TableName("gis_compose_task")
public class ComposeTaskEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "组合任务id")
	private Long composeId;

	@Schema(title = "任务id")
	private Long taskId;

	@Schema(title = "排序")
	private Integer sort;

}
