package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 部门表
 *
 * @author gzw
 */
@Schema(title = "部门表")
@Data
public class DepartmentSaveDTO {

	@NotNull(groups = Update.class)
	@Schema(title = "部门ID")
	private Long id;

	@Schema(title = "上级部门ID")
	private Long parentId;

	@Schema(title = "部门名称")
	private String name;

	@Schema(title = "部门编码")
	private String code;

	@Schema(title = "部门层级")
	private Integer depth;

	@Schema(title = "排序")
	private Integer sortOrder;

	@Schema(title = "是否启用")
	private Boolean isActive;

	@Schema(title = "扩展字段")
	private Object metadata;

	@Schema(title = "备注")
	private String remark;

	@Schema(title = "创建时间")
	private Timestamp createdAt;

	@Schema(title = "更新时间")
	private Timestamp updatedAt;

}
