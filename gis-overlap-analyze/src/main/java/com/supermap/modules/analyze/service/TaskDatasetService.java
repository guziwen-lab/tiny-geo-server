package com.supermap.modules.analyze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyze.entity.TaskDatasetEntity;
import com.supermap.modules.analyze.dto.TaskDatasetDTO;
import com.supermap.modules.analyze.dto.TaskDatasetSaveDTO;

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

