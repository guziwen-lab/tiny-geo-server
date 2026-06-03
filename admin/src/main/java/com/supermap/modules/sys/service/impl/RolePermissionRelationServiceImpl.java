package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.common.util.CollectionUtils;
import com.supermap.modules.sys.dao.RolePermissionRelationDao;
import com.supermap.modules.sys.entity.RolePermissionRelationEntity;
import com.supermap.modules.sys.service.RolePermissionRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("rolePermissionRelationService")
public class RolePermissionRelationServiceImpl extends ServiceImpl<RolePermissionRelationDao, RolePermissionRelationEntity> implements RolePermissionRelationService {

    @Override
    public void removeByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds))
            return;
        remove(new LambdaQueryWrapper<RolePermissionRelationEntity>()
                .in(RolePermissionRelationEntity::getRoleId, roleIds));
    }

    @Override
    public void removeByPermissionIds(List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds))
            return;
        remove(new LambdaQueryWrapper<RolePermissionRelationEntity>()
                .in(RolePermissionRelationEntity::getPermissionId, permissionIds));
    }

}