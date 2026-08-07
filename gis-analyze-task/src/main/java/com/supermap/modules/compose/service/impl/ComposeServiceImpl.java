package com.supermap.modules.compose.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.LayerInfo;
import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.JSON;
import com.supermap.config.DatasetProperties;
import com.supermap.enums.TaskStatus;
import com.supermap.modules.analyzetask.dto.ComposeTaskDTO;
import com.supermap.modules.analyzetask.dto.TaskDatasetSaveDTO;
import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.service.TaskDatasetService;
import com.supermap.modules.analyzetask.service.TaskService;
import com.supermap.modules.analyzetask.service.impl.TaskServiceImpl;
import com.supermap.modules.compose.vo.ComposeVO;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.support.AnalysisContextBuilder;
import com.supermap.support.LayerInfoBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.compose.dao.ComposeDao;
import com.supermap.modules.compose.entity.ComposeEntity;
import com.supermap.modules.compose.service.ComposeService;
import com.supermap.modules.compose.dto.ComposeDTO;
import com.supermap.modules.compose.dto.ComposeSaveDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service("composeService")
public class ComposeServiceImpl extends ServiceImpl<ComposeDao, ComposeEntity> implements ComposeService {

    private final TaskService taskService;
    private final DatasetService datasetService;
    private final TaskDatasetService taskDatasetService;
    private final AnalysisContextBuilder analysisExecutor;
    private final DatasetProperties datasetProperties;

    @Override
    public Page<ComposeEntity> queryPage(ComposeDTO dto) {
        LambdaQueryWrapper<ComposeEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(ComposeSaveDTO dto) {
        ComposeEntity composeEntity = new ComposeEntity();
        BeanUtils.copyProperties(dto, composeEntity);
        save(composeEntity);
        return composeEntity.getId();
    }

    @Override
    public void updateDTOById(ComposeSaveDTO dto) {
        ComposeEntity composeEntity = new ComposeEntity();
        BeanUtils.copyProperties(dto, composeEntity);
        updateById(composeEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <T extends AnalysisParam> ComposeVO<T> createByCompose(ComposeTaskDTO<T> dto) {
        // 验证数据集
        List<DatasetEntity> datasetEntities = datasetService.listByIds(dto.getDatasetIds().stream()
                .map(TaskDatasetSaveDTO::getDatasetId).toList());
        if (datasetEntities.size() != dto.getDatasetIds().size())
            throw new IllegalArgumentException("Dataset not found");

        // 保存任务
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskName(dto.getTaskName());
        taskEntity.setAnalysisType(dto.getAnalysisType());
        taskEntity.setTaskParam(JSON.toJSONString(dto.getTaskParam()));
        taskEntity.setStatus(TaskStatus.PROCESSING);
        taskEntity.setCreatedAt(Instant.now());
        taskService.save(taskEntity);

        // 保存任务数据集关系
        List<TaskDatasetEntity> taskDatasetEntities = TaskServiceImpl.getTaskDatasetEntities(dto.getDatasetIds(), taskEntity);
        taskDatasetService.saveBatch(taskDatasetEntities);

        // 构建图层
        List<LayerInfo> layerInfos = datasetEntities.stream()
                .map(LayerInfoBuilder::fromDatasetEntity)
                .toList();

        // 构建分析上下文
        AnalysisContext<T> context = analysisExecutor.buildAnalysisContext(
                layerInfos,
                dto.getTaskParam(),
                "analyze_" + taskEntity.getId());

        ComposeVO<T> composeVO = new ComposeVO<>();
        composeVO.setTaskEntity(taskEntity);
        composeVO.setAnalysisContext(context);
        return composeVO;
    }

}