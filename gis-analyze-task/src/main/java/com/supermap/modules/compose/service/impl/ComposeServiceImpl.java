package com.supermap.modules.compose.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.compose.dao.ComposeDao;
import com.supermap.modules.compose.entity.ComposeEntity;
import com.supermap.modules.compose.service.ComposeService;
import com.supermap.modules.compose.dto.ComposeDTO;
import com.supermap.modules.compose.dto.ComposeSaveDTO;

@Service("composeService")
public class ComposeServiceImpl extends ServiceImpl<ComposeDao, ComposeEntity> implements ComposeService {

    @Override
    public Page<ComposeEntity> queryPage(ComposeDTO dto) {
        LambdaQueryWrapper<ComposeEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(ComposeSaveDTO dto) {
        ComposeEntity composeEntity = new ComposeEntity();
        BeanUtils.copyProperties(dto, composeEntity);
        save(composeEntity);
        return composeEntity.getId();
    }

    @Override
    public void updateDTOById(ComposeSaveDTO dto) {
        ComposeEntity composeEntity = new ComposeEntity();
        BeanUtils.copyProperties(dto, composeEntity);
        updateById(composeEntity);
    }

}