package com.supermap.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

/**
 * ${comments}
 *
 * @author gzw
 */
@Schema(title = "${comments}")
@Data
public class ExportTaskSaveDTO {

	@NotNull(groups = Update.class)
	@Schema(title = "$column.comments")
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
