package com.supermap.modules.analyze.dto;

import java.time.Instant;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${comments}
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "${comments}")
@Data
public class ExportTaskDTO extends PageParam {

	@Schema(title = "开始时间")
	private Instant startTime;

	@Schema(title = "结束时间")
	private Instant endTime;

	@Schema(title = "id")
	private Long id;

	@Schema(title = "需要导出的空间表名")
	private String tableName;

	@Schema(title = "导出类型：SHP, GDB")
	private String exportType;

	@Schema(title = "导出的目标文件/文件夹路径")
	private String targetPath;

	@Schema(title = "任务状态：PROCESSING, SUCCESS, FAILED")
	private String status;

	@Schema(title = "失败时的错误堆栈/信息")
	private String message;

	@Schema(title = "$column.comments")
	private Instant createdAt;

	@Schema(title = "$column.comments")
	private Instant finishedAt;

}
