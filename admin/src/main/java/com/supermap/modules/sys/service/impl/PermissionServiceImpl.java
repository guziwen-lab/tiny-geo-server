package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.common.util.BeanUtils;
import com.supermap.modules.sys.dao.PermissionDao;
import com.supermap.modules.sys.dto.PermissionDTO;
import com.supermap.modules.sys.dto.PermissionSaveDTO;
import com.supermap.modules.sys.entity.PermissionEntity;
import com.supermap.modules.sys.service.PermissionService;
import com.supermap.modules.sys.service.RolePermissionRelationService;
import com.supermap.modules.sys.vo.PermissionVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service("permissionService")
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, PermissionEntity> implements PermissionService {

    private final RolePermissionRelationService rolePermissionRelationService;

    @Override
    public Page<PermissionEntity> queryPage(PermissionDTO dto) {
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<>(PermissionEntity.class);
        return page(dto.page(), wrapper);
    }

    @Override
    public List<PermissionEntity> getByRoleId(Long roleId) {
        return baseMapper.getByRoleId(roleId);
    }

    @Override
    public Set<PermissionEntity> getByUserId(Long userId) {
        return baseMapper.getByUserId(userId);
    }

    @Override
    public Long saveDTO(PermissionSaveDTO dto) {
        /*PermissionEntity exists = getOne(new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getPermsKey, dto.getPermsKey()));
        if (exists != null) {
            throw new RuntimeException("权限key已存在");
        }*/

        PermissionEntity permissionEntity = new PermissionEntity();
        BeanUtils.copyProperties(dto, permissionEntity);
        save(permissionEntity);
        return permissionEntity.getPermissionId();
    }

    @Override
    public void updateDTOById(PermissionSaveDTO dto) {
        PermissionEntity permissionEntity = new PermissionEntity();
        BeanUtils.copyProperties(dto, permissionEntity);
        updateById(permissionEntity);
    }

    @Override
    public List<PermissionVO> all() {
        List<PermissionEntity> list = list(new LambdaQueryWrapper<>(PermissionEntity.class)
                .orderByAsc(PermissionEntity::getSort)
                .orderByAsc(PermissionEntity::getCreateTime));

        List<PermissionVO> all = BeanUtils.copyToList(list, PermissionVO.class);
        List<PermissionVO> root = all.stream()
                .filter(permissionVO -> permissionVO.getParentId() == null)
                .toList();

        root.forEach(permissionVO -> setChildren(permissionVO, all));

        return root;
    }

    private void setChildren(PermissionVO permissionVO, List<PermissionVO> all) {
        List<PermissionVO> children = all.stream()
                .filter(vo -> permissionVO.getPermissionId().equals(vo.getParentId()))
                .toList();
        permissionVO.setChildren(children);
        children.forEach(vo -> setChildren(vo, all));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> permissionIds) {
        removeByIds(permissionIds);
        rolePermissionRelationService.removeByPermissionIds(permissionIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchUpdateSortAndLevel(List<PermissionSaveDTO> dto) {
        List<PermissionEntity> permissionEntities = BeanUtils.copyToList(dto, PermissionEntity.class);
        int count = baseMapper.updateBatchById(permissionEntities);
        if (count != permissionEntities.size()) {
            throw new RuntimeException("更新失败");
        }
    }

}