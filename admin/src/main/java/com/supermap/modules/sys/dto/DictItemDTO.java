package com.supermap.modules.sys.dto;

import java.sql.Timestamp;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典项表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "字典项表")
@Data
public class DictItemDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

	@Schema(title = "主键")
	private Long dictItemId;

	@Schema(title = "字典id")
	private Long dictId;

	@Schema(title = "上级字典项id")
	private Long parentId;

	@Schema(title = "字典项名称")
	private String name;

    @Schema(title = "字典项编码")
    private String code;

	@Schema(title = "排序")
	private Integer sort;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
