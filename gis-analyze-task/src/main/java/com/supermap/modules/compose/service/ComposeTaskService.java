package com.supermap.modules.compose.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.compose.entity.ComposeTaskEntity;
import com.supermap.modules.compose.dto.ComposeTaskDTO;
import com.supermap.modules.compose.dto.ComposeTaskSaveDTO;

/**
 * 组合任务任务关联表
 *
 * @author gzw
 */
public interface ComposeTaskService extends IService<ComposeTaskEntity> {

    Page<ComposeTaskEntity> queryPage(ComposeTaskDTO dto);

    Long saveDTO(ComposeTaskSaveDTO dto);

    void updateDTOById(ComposeTaskSaveDTO dto);

}

