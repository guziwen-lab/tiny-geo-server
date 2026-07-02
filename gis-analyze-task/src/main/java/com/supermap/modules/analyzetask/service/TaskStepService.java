package com.supermap.modules.analyzetask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyzetask.entity.TaskStepEntity;
import com.supermap.modules.analyzetask.dto.TaskStepDTO;
import com.supermap.modules.analyzetask.dto.TaskStepSaveDTO;

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

