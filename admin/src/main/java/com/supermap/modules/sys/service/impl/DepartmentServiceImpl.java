package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.modules.sys.vo.DepartmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.DepartmentDao;
import com.supermap.modules.sys.entity.DepartmentEntity;
import com.supermap.modules.sys.service.DepartmentService;
import com.supermap.modules.sys.dto.DepartmentDTO;
import com.supermap.modules.sys.dto.DepartmentSaveDTO;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("departmentService")
public class DepartmentServiceImpl extends ServiceImpl<DepartmentDao, DepartmentEntity> implements DepartmentService {

    @Override
    public Page<DepartmentVO> queryPage(DepartmentDTO dto) {
        return baseMapper.page(dto.page(), dto);
    }

    @Override
    public List<DepartmentVO> subordinate(DepartmentDTO dto) {
        return baseMapper.subordinate(dto);
    }

    @Override
    public Long saveDTO(DepartmentSaveDTO dto) {
        DepartmentEntity departmentEntity = new DepartmentEntity();
        BeanUtils.copyProperties(dto, departmentEntity);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        departmentEntity.setCreatedAt(now);
        departmentEntity.setUpdatedAt(now);
        try {
            save(departmentEntity);
        } catch (DataIntegrityViolationException ex) {
            log.debug("部门编码已存在", ex);
            throw new DataIntegrityViolationException("部门编码已存在");
        }
        return departmentEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDTOById(DepartmentSaveDTO dto) {
        DepartmentEntity departmentEntity = new DepartmentEntity();
        BeanUtils.copyProperties(dto, departmentEntity);
        departmentEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        updateById(departmentEntity);

        if (departmentEntity.getIsActive() != null && !departmentEntity.getIsActive()) {
            // 更新它的下级
            baseMapper.disableChildrenById(departmentEntity.getId());
        }
    }

    @Override
    public List<DepartmentVO> tree(Boolean isActive) {
        List<DepartmentEntity> list = list(new LambdaQueryWrapper<DepartmentEntity>()
                .eq(isActive != null, DepartmentEntity::getIsActive, isActive)
                .orderByAsc(DepartmentEntity::getSortOrder)
                .orderByAsc(DepartmentEntity::getCreatedAt));
        List<DepartmentVO> all = BeanUtils.copyToList(list, DepartmentVO.class);

        List<DepartmentVO> root = all.stream()
                .filter(i -> i.getDepth() == 0)
                .toList();
        root.forEach(i -> setChildren(i, all));
        return root;
    }

    @Override
    public List<DepartmentEntity> getByUserId(Long userId) {
        return baseMapper.getDepartmentByUserId(userId);
    }

    private void setChildren(DepartmentVO departmentVO, List<DepartmentVO> all) {
        List<DepartmentVO> children = all.stream()
                .filter(i -> Objects.equals(departmentVO.getId(), i.getParentId()))
                .toList();
        departmentVO.setChildren(children);
        children.forEach(i -> setChildren(i, all));
    }

    @Override
    public List<DepartmentEntity> getWithSubordinatesByCodes(Collection<String> codes) {
        return baseMapper.getWithSubordinatesByCodes(codes);
    }

}