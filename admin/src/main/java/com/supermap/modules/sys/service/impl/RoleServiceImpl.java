package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.CollectionUtils;
import com.supermap.modules.sys.dao.RoleDao;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.dto.RoleSaveDTO;
import com.supermap.modules.sys.entity.PermissionEntity;
import com.supermap.modules.sys.entity.RoleEntity;
import com.supermap.modules.sys.entity.RolePermissionRelationEntity;
import com.supermap.modules.sys.service.LoginUserService;
import com.supermap.modules.sys.service.RolePermissionRelationService;
import com.supermap.modules.sys.service.RoleService;
import com.supermap.modules.sys.vo.RoleVO;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service("roleService")
@AllArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleDao, RoleEntity> implements RoleService {

    private final RolePermissionRelationService rolePermissionRelationService;

    private final PermissionServiceImpl permissionService;

    private final LoginUserService loginUserService;

    @Override
    public Page<RoleEntity> queryPage(SearchDTO dto) {
        return baseMapper.page(dto.page(), dto);
    }

    @Override
    public Set<String> getRoleNamesByUserId(Long userId) {
        return baseMapper.getRoleNamesByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveDTO(RoleSaveDTO dto) {
        RoleEntity roleName = getByRoleName(dto.getRoleName());
        if (roleName != null) {
            throw new IllegalArgumentException("角色名已存在");
        }

        RoleEntity roleEntity = new RoleEntity();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        roleEntity.setCreateTime(now);
        roleEntity.setUpdateTime(now);
        BeanUtils.copyProperties(dto, roleEntity);

        try {
            save(roleEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("角色名已存在");
        }

        savePermissionByRoleId(roleEntity.getRoleId(), dto.getPermissionIds());

        return roleEntity.getRoleId();
    }

    private void savePermissionByRoleId(Long roleId, List<Long> permissionIds) {
        rolePermissionRelationService.removeByRoleIds(Collections.singletonList(roleId));

        if (CollectionUtils.isEmpty(permissionIds))
            return;

        long count = permissionService.count(new LambdaQueryWrapper<PermissionEntity>()
                .in(PermissionEntity::getPermissionId, permissionIds));
        if (count != permissionIds.size())
            throw new IllegalArgumentException("权限不存在");

        List<RolePermissionRelationEntity> relationEntities = permissionIds.stream()
                .map(item -> {
                    RolePermissionRelationEntity relationEntity = new RolePermissionRelationEntity();
                    relationEntity.setRoleId(roleId);
                    relationEntity.setPermissionId(item);
                    return relationEntity;
                }).toList();
        rolePermissionRelationService.saveBatch(relationEntities);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDTO(RoleSaveDTO dto) {
        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(dto, roleEntity);
        roleEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        updateById(roleEntity);

        savePermissionByRoleId(dto.getRoleId(), dto.getPermissionIds());

        // 修改了角色信息要更新这个角色对应的所有用户的登录信息
        loginUserService.refreshLoginUserInfoByRoleId(dto.getRoleId());
    }

    @Override
    public List<RoleEntity> all() {
        return list(new LambdaQueryWrapper<>(RoleEntity.class)
                .orderByDesc(RoleEntity::getCreateTime));
    }

    @Override
    public List<RoleEntity> getByUserId(Long userId) {
        return baseMapper.getRoleByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> roleIds) {
        removeByIds(roleIds);
        rolePermissionRelationService.removeByRoleIds(roleIds);
    }

    @Override
    public RoleVO getVOById(Long roleId) {
        RoleEntity role = getById(roleId);
        List<PermissionEntity> permissions = permissionService.getByRoleId(roleId);

        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        roleVO.setPermissions(permissions);
        return roleVO;
    }

    private RoleEntity getByRoleName(String roleName) {
        return getOne(new LambdaQueryWrapper<>(RoleEntity.class)
                .eq(RoleEntity::getRoleName, roleName));
    }

}