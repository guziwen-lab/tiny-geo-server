package com.supermap.modules.sys.dto;

import java.sql.Timestamp;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户部门关系表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "用户部门关系表")
@Data
public class UserDepartmentRelationDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "用户id")
	private Long userId;

	@Schema(title = "部门id")
	private Long departmentId;

}
