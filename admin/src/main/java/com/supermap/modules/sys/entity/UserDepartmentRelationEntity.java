package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户部门关系表
 *
 * @author gzw
 */
@Schema(title = "用户部门关系表")
@Data
@TableName("sys_user_department_relation")
public class UserDepartmentRelationEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "用户id")
	private Long userId;

	@Schema(title = "部门id")
	private Long departmentId;

}
