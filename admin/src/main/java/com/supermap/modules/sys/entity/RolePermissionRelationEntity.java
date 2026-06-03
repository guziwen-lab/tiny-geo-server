package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色权限关系表
 *
 * @author gzw
 */
@Schema(title = "角色权限关系表")
@Data
@TableName("sys_role_permission_relation")
public class RolePermissionRelationEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private Long id;
    /**
     * 角色id
     */
    @Schema(title = "角色id")
    private Long roleId;
    /**
     * 权限id
     */
    @Schema(title = "权限id")
    private Long permissionId;

}
