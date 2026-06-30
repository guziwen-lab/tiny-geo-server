package com.supermap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.entity.TaskStepEntity;
import com.supermap.dto.TaskStepDTO;
import com.supermap.dto.TaskStepSaveDTO;

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

