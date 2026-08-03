package com.supermap.modules.compose.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.compose.entity.ComposeEntity;
import com.supermap.modules.compose.dto.ComposeDTO;
import com.supermap.modules.compose.dto.ComposeSaveDTO;

/**
 * 组合任务表
 *
 * @author gzw
 */
public interface ComposeService extends IService<ComposeEntity> {

    Page<ComposeEntity> queryPage(ComposeDTO dto);

    Long saveDTO(ComposeSaveDTO dto);

    void updateDTOById(ComposeSaveDTO dto);

}

