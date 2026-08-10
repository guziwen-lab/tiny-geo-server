package com.supermap.task.modules.compose.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.task.modules.compose.entity.ComposeStepEntity;
import com.supermap.task.modules.compose.dto.ComposeStepDTO;
import com.supermap.task.modules.compose.dto.ComposeStepSaveDTO;

/**
 * 组合任务任务关联表
 *
 * @author gzw
 */
public interface ComposeStepService extends IService<ComposeStepEntity> {

    Page<ComposeStepEntity> queryPage(ComposeStepDTO dto);

    Long saveDTO(ComposeStepSaveDTO dto);

    void updateDTOById(ComposeStepSaveDTO dto);

}

