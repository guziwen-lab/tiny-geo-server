package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 文件管理表
 *
 * @author gzw
 */
@Schema(title = "文件管理表")
@Data
public class FileSaveDTO {

	@NotNull(groups = Update.class)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "文件名称")
	private String fileName;

	@Schema(title = "文件路径")
	private String filePath;

	@Schema(title = "文件MIME类型")
	private String fileType;

	@Schema(title = "文件大小 (字节)")
	private Long fileSize;

	@Schema(title = "存储方式 (local/oss/cos...)")
	private String storageType;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
