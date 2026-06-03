package com.supermap.modules.analyze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyze.dto.StartTaskDTO;
import com.supermap.modules.analyze.entity.TaskEntity;
import com.supermap.modules.analyze.dto.TaskDTO;
import com.supermap.modules.analyze.dto.TaskSaveDTO;

/**
 * 任务表
 *
 * @author gzw
 */
public interface TaskService extends IService<TaskEntity> {

    Page<TaskEntity> queryPage(TaskDTO dto);

    Long create(TaskSaveDTO dto);

    void updateDTOById(TaskSaveDTO dto);

    void start(Long taskId, StartTaskDTO dto);

}

