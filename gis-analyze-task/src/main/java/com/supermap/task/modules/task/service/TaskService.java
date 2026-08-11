package com.supermap.task.modules.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.task.modules.task.dto.StartTaskDTO;
import com.supermap.task.modules.task.entity.TaskEntity;
import com.supermap.task.modules.task.dto.TaskDTO;
import com.supermap.task.modules.task.dto.TaskSaveDTO;

/**
 * 任务表
 *
 * @author gzw
 */
public interface TaskService extends IService<TaskEntity> {

    TaskEntity create(TaskSaveDTO dto);

    void start(Long taskId, StartTaskDTO dto);

    Page<TaskEntity> queryPage(TaskDTO dto);

}

