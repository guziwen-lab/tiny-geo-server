package com.supermap.modules.analyze.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图层引用表
 *
 * @author gzw
 */
@Schema(title = "图层引用表")
@Data
@TableName("gis_task_dataset")
public class TaskDatasetEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "任务ID")
	private Long taskId;

	@Schema(title = "数据集ID")
	private Long datasetId;

}
