package com.supermap.modules.analyze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyze.entity.TaskStepEntity;
import com.supermap.modules.analyze.dto.TaskStepDTO;
import com.supermap.modules.analyze.dto.TaskStepSaveDTO;

/**
 * 任务执行记录表
 *
 * @author gzw
 */
public interface TaskStepService extends IService<TaskStepEntity> {

    Page<TaskStepEntity> queryPage(TaskStepDTO dto);

    Long saveDTO(TaskStepSaveDTO dto);

    void updateDTOById(TaskStepSaveDTO dto);

}

