package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.modules.sys.entity.RolePermissionRelationEntity;

import java.util.List;

/**
 * 角色权限关系表
 *
 * @author gzw
 */
public interface RolePermissionRelationService extends IService<RolePermissionRelationEntity> {

    void removeByRoleIds(List<Long> roleIds);

    void removeByPermissionIds(List<Long> permissionIds);

}

