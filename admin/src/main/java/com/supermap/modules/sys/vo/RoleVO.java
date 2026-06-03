package com.supermap.modules.sys.vo;

import com.supermap.modules.sys.entity.PermissionEntity;
import com.supermap.modules.sys.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleVO extends RoleEntity {

    @Schema(title = "权限名称")
    private String permissionNames;

    @Schema(title = "权限")
    private List<PermissionEntity> permissions;

}
