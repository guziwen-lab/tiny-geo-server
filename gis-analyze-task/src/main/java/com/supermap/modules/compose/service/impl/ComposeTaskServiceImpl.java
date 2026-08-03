package com.supermap.modules.compose.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.compose.dao.ComposeTaskDao;
import com.supermap.modules.compose.entity.ComposeTaskEntity;
import com.supermap.modules.compose.service.ComposeTaskService;
import com.supermap.modules.compose.dto.ComposeTaskDTO;
import com.supermap.modules.compose.dto.ComposeTaskSaveDTO;

@Service("composeTaskService")
public class ComposeTaskServiceImpl extends ServiceImpl<ComposeTaskDao, ComposeTaskEntity> implements ComposeTaskService {

    @Override
    public Page<ComposeTaskEntity> queryPage(ComposeTaskDTO dto) {
        LambdaQueryWrapper<ComposeTaskEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(ComposeTaskSaveDTO dto) {
        ComposeTaskEntity composeTaskEntity = new ComposeTaskEntity();
        BeanUtils.copyProperties(dto, composeTaskEntity);
        save(composeTaskEntity);
        return composeTaskEntity.getId();
    }

    @Override
    public void updateDTOById(ComposeTaskSaveDTO dto) {
        ComposeTaskEntity composeTaskEntity = new ComposeTaskEntity();
        BeanUtils.copyProperties(dto, composeTaskEntity);
        updateById(composeTaskEntity);
    }

}