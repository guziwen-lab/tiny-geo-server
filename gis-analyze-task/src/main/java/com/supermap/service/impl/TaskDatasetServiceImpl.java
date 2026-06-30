package com.supermap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.dao.TaskDatasetDao;
import com.supermap.entity.TaskDatasetEntity;
import com.supermap.service.TaskDatasetService;
import com.supermap.dto.TaskDatasetDTO;
import com.supermap.dto.TaskDatasetSaveDTO;

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
    public List<TaskDatasetEntity> getByTaskId(Long taskId) {
        return list(new LambdaQueryWrapper<TaskDatasetEntity>().eq(TaskDatasetEntity::getTaskId, taskId));
    }

}