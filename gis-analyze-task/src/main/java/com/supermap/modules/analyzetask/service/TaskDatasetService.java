package com.supermap.modules.analyzetask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;
import com.supermap.modules.analyzetask.dto.TaskDatasetDTO;
import com.supermap.modules.analyzetask.dto.TaskDatasetSaveDTO;
import com.supermap.modules.dataset.entity.DatasetEntity;

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

    /**
     * 根据任务id获取数据集
     *
     * @param taskId 任务id
     * @return 数据集列表
     */
    List<DatasetEntity> getDatasetEntityByTaskId(Long taskId);

}

