package com.supermap.modules.sys.dto;

import java.sql.Timestamp;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "字典表")
@Data
public class DictDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

	@Schema(title = "主键")
	private Long dictId;

	@Schema(title = "字典名称")
	private String name;

	@Schema(title = "字典描述")
	private String description;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
