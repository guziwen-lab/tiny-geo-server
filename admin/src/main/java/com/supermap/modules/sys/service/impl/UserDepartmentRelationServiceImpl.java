package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.UserDepartmentRelationDao;
import com.supermap.modules.sys.entity.UserDepartmentRelationEntity;
import com.supermap.modules.sys.service.UserDepartmentRelationService;
import com.supermap.modules.sys.dto.UserDepartmentRelationDTO;
import com.supermap.modules.sys.dto.UserDepartmentRelationSaveDTO;

@Service("userDepartmentRelationService")
public class UserDepartmentRelationServiceImpl extends ServiceImpl<UserDepartmentRelationDao, UserDepartmentRelationEntity> implements UserDepartmentRelationService {

    @Override
    public Page<UserDepartmentRelationEntity> queryPage(UserDepartmentRelationDTO dto) {
        LambdaQueryWrapper<UserDepartmentRelationEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(UserDepartmentRelationSaveDTO dto) {
        UserDepartmentRelationEntity userDepartmentRelationEntity = new UserDepartmentRelationEntity();
        BeanUtils.copyProperties(dto, userDepartmentRelationEntity);
        save(userDepartmentRelationEntity);
        return userDepartmentRelationEntity.getId();
    }

    @Override
    public void updateDTOById(UserDepartmentRelationSaveDTO dto) {
        UserDepartmentRelationEntity userDepartmentRelationEntity = new UserDepartmentRelationEntity();
        BeanUtils.copyProperties(dto, userDepartmentRelationEntity);
        updateById(userDepartmentRelationEntity);
    }

    @Override
    public void removeByUserId(Long userId) {
        remove(new LambdaQueryWrapper<UserDepartmentRelationEntity>()
                .eq(UserDepartmentRelationEntity::getUserId, userId));
    }

}