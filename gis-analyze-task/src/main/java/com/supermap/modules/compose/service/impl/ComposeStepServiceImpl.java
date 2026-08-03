package com.supermap.modules.compose.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.compose.dao.ComposeStepDao;
import com.supermap.modules.compose.entity.ComposeStepEntity;
import com.supermap.modules.compose.service.ComposeStepService;
import com.supermap.modules.compose.dto.ComposeStepDTO;
import com.supermap.modules.compose.dto.ComposeStepSaveDTO;

@Service("composeTaskService")
public class ComposeStepServiceImpl extends ServiceImpl<ComposeStepDao, ComposeStepEntity> implements ComposeStepService {

    @Override
    public Page<ComposeStepEntity> queryPage(ComposeStepDTO dto) {
        LambdaQueryWrapper<ComposeStepEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(ComposeStepSaveDTO dto) {
        ComposeStepEntity composeStepEntity = new ComposeStepEntity();
        BeanUtils.copyProperties(dto, composeStepEntity);
        save(composeStepEntity);
        return composeStepEntity.getId();
    }

    @Override
    public void updateDTOById(ComposeStepSaveDTO dto) {
        ComposeStepEntity composeStepEntity = new ComposeStepEntity();
        BeanUtils.copyProperties(dto, composeStepEntity);
        updateById(composeStepEntity);
    }

}