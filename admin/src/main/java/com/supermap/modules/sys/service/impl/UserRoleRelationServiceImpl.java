package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.common.util.CollectionUtils;
import com.supermap.modules.sys.dao.UserRoleRelationDao;
import com.supermap.modules.sys.entity.UserRoleRelationEntity;
import com.supermap.modules.sys.service.UserRoleRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userRoleRelationService")
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationDao, UserRoleRelationEntity> implements UserRoleRelationService {

    @Override
    public void removeByUserId(Long userId) {
        remove(new LambdaQueryWrapper<UserRoleRelationEntity>().eq(UserRoleRelationEntity::getUserId, userId));
    }

    @Override
    public void removeByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds))
            return;
        remove(new LambdaQueryWrapper<UserRoleRelationEntity>().in(UserRoleRelationEntity::getUserId, userIds));
    }

    @Override
    public List<Long> getUserIdByRoleId(Long roleId) {
        return list(new LambdaQueryWrapper<UserRoleRelationEntity>()
                .eq(UserRoleRelationEntity::getRoleId, roleId)
                .select(UserRoleRelationEntity::getUserId))
                .stream()
                .map(UserRoleRelationEntity::getUserId)
                .toList();
    }

}