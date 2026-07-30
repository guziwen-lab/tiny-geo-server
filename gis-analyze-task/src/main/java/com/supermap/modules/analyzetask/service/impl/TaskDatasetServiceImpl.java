package com.supermap.modules.analyzetask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.modules.dataset.entity.DatasetEntity;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.analyzetask.dao.TaskDatasetDao;
import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;
import com.supermap.modules.analyzetask.service.TaskDatasetService;
import com.supermap.modules.analyzetask.dto.TaskDatasetDTO;
import com.supermap.modules.analyzetask.dto.TaskDatasetSaveDTO;

import java.util.List;

@Service("taskDatasetService")
public class TaskDatasetServiceImpl extends ServiceImpl<TaskDatasetDao, TaskDatasetEntity> implements TaskDatasetService {

    @Override
    public Page<TaskDatasetEntity> queryPage(TaskDatasetDTO dto) {
        LambdaQueryWrapper<TaskDatasetEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(TaskDatasetSaveDTO dto) {
        TaskDatasetEntity taskDatasetEntity = new TaskDatasetEntity();
        BeanUtils.copyProperties(dto, taskDatasetEntity);
        save(taskDatasetEntity);
        return taskDatasetEntity.getId();
    }

    @Override
    public void updateDTOById(TaskDatasetSaveDTO dto) {
        TaskDatasetEntity taskDatasetEntity = new TaskDatasetEntity();
        BeanUtils.copyProperties(dto, taskDatasetEntity);
        updateById(taskDatasetEntity);
    }

    @Override
    public List<DatasetEntity> getDatasetEntityByTaskId(Long taskId) {
        return baseMapper.getDatasetEntityByTaskId(taskId);
    }

}