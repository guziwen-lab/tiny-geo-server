package com.supermap.modules.sys.vo;

import com.supermap.modules.sys.entity.UserDepartmentRelationEntity;
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
public class UserDepartmentRelationVO extends UserDepartmentRelationEntity {

}
