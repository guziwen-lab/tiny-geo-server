package com.supermap.task.modules.compose.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.analyze.AnalysisParam;
import com.supermap.task.modules.task.dto.ComposeTaskDTO;
import com.supermap.task.modules.compose.entity.ComposeEntity;
import com.supermap.task.modules.compose.dto.ComposeDTO;
import com.supermap.task.modules.compose.dto.ComposeSaveDTO;
import com.supermap.task.modules.compose.vo.ComposeVO;

/**
 * 组合任务表
 *
 * @author gzw
 */
public interface ComposeService extends IService<ComposeEntity> {

    Page<ComposeEntity> queryPage(ComposeDTO dto);

    <T extends AnalysisParam> ComposeVO<T> createTask(ComposeTaskDTO<T> dto, Integer sort);

    ComposeEntity createCompose(ComposeSaveDTO dto);

}

