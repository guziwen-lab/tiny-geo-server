package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户部门关系表
 *
 * @author gzw
 */
@Schema(title = "用户部门关系表")
@Data
public class UserDepartmentRelationSaveDTO {

	@NotNull(groups = Update.class)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "用户id")
	private Long userId;

	@Schema(title = "部门id")
	private Long departmentId;

}
