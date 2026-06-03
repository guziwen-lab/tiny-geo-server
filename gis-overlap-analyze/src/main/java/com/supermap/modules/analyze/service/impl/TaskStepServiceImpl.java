package com.supermap.modules.analyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.analyze.dao.TaskStepDao;
import com.supermap.modules.analyze.entity.TaskStepEntity;
import com.supermap.modules.analyze.service.TaskStepService;
import com.supermap.modules.analyze.dto.TaskStepDTO;
import com.supermap.modules.analyze.dto.TaskStepSaveDTO;

@Service("taskStepService")
public class TaskStepServiceImpl extends ServiceImpl<TaskStepDao, TaskStepEntity> implements TaskStepService {

    @Override
    public Page<TaskStepEntity> queryPage(TaskStepDTO dto) {
        LambdaQueryWrapper<TaskStepEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(TaskStepSaveDTO dto) {
        TaskStepEntity taskStepEntity = new TaskStepEntity();
        BeanUtils.copyProperties(dto, taskStepEntity);
        save(taskStepEntity);
        return taskStepEntity.getId();
    }

    @Override
    public void updateDTOById(TaskStepSaveDTO dto) {
        TaskStepEntity taskStepEntity = new TaskStepEntity();
        BeanUtils.copyProperties(dto, taskStepEntity);
        updateById(taskStepEntity);
    }

}