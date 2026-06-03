package com.supermap.modules.sys.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supermap.modules.sys.entity.DepartmentEntity;
import com.supermap.modules.sys.entity.RoleEntity;
import com.supermap.modules.sys.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户表VO
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "用户表VO")
@Data
public class UserVO extends UserEntity {

	@JsonIgnore
	@Schema(title = "密码")
	private String password;

    @Schema(title = "角色名称")
    private String roleNames;

    @Schema(title = "角色")
    private List<RoleEntity> roleEntities;

    @Schema(title = "部门")
    private List<DepartmentEntity> departmentEntities;

}
