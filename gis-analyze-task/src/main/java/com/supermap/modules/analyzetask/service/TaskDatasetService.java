package com.supermap.modules.analyzetask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;
import com.supermap.modules.analyzetask.dto.TaskDatasetDTO;
import com.supermap.modules.analyzetask.dto.TaskDatasetSaveDTO;

import java.util.List;

/**
 * 图层引用表
 *
 * @author gzw
 */
public interface TaskDatasetService extends IService<TaskDatasetEntity> {

    Page<TaskDatasetEntity> queryPage(TaskDatasetDTO dto);

    Long saveDTO(TaskDatasetSaveDTO dto);

    void updateDTOById(TaskDatasetSaveDTO dto);

    List<TaskDatasetEntity> getByTaskId(Long taskId);

}

