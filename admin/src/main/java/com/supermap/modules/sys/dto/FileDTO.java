package com.supermap.modules.sys.dto;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 文件管理表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "文件管理表")
@Data
public class FileDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

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
