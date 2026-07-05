package com.supermap.modules.analyzetask.service;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisParam;
import com.supermap.AnalysisResult;
import com.supermap.AnalysisStep;
import com.supermap.common.util.CollectionUtils;
import com.supermap.enums.TaskStatus;
import com.supermap.enums.UploadStatus;
import com.supermap.modules.analyzetask.dao.TaskDao;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.entity.TaskStepEntity;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.service.DatasetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskStatusUpdateService {

    private final TaskDao taskDao;

    private final DatasetService datasetService;

    private final TaskStepService taskStepService;

    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(TaskEntity task, AnalysisResult result, AnalysisContext<AnalysisParam> context) {
        // 保存步骤
        saveSteps(task, context.getSteps());

        // 结果记录到数据集
        DatasetEntity resultDatasetEntity = saveResultToDataset(result, context);

        TaskEntity update = taskDao.selectById(task.getId());
        update.setStatus(TaskStatus.SUCCESS);
        update.setResultTableName(result.getResultTableName());
        update.setFeatureCount(result.getFeatureCount());
        update.setCost(result.getCost());
        update.setFinishedAt(Instant.now());
        update.setResultDatasetId(resultDatasetEntity.getId());
        taskDao.updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markFailed(Long taskId, String message) {
        TaskEntity task = taskDao.selectById(taskId);
        task.setStatus(TaskStatus.FAILED);
        task.setFinishedAt(Instant.now());
        task.setMessage(message);
        taskDao.updateById(task);
    }

    private void saveSteps(TaskEntity task, List<AnalysisStep> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }

        List<TaskStepEntity> stepEntities = new ArrayList<>(steps.size());
        for (AnalysisStep step : steps) {
            // 保存步骤
            TaskStepEntity stepEntity = new TaskStepEntity();
            stepEntity.setTaskId(task.getId());
            stepEntity.setStepNo(step.getStepNo());
            stepEntity.setInputTable(step.getInputTable());
            stepEntity.setOverlayTable(step.getOverlayTable());
            stepEntity.setOutputTable(step.getOutputTable());
            stepEntities.add(stepEntity);
        }

        taskStepService.saveBatch(stepEntities);
    }

    private DatasetEntity saveResultToDataset(AnalysisResult result, AnalysisContext<AnalysisParam> context) {
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(result.getResultTableName());
        datasetEntity.setLayerName(result.getResultLayerName());
        datasetEntity.setSchemaName(result.getSchemaName());
        datasetEntity.setTableName(result.getResultTableName());
        datasetEntity.setGeomType(result.getGeomType());
        datasetEntity.setSrid(result.getSrid());
        datasetEntity.setFeatureCount(result.getFeatureCount());
        datasetEntity.setCreatedAt(Instant.now());
        datasetEntity.setStatus(UploadStatus.SUCCESS);
        datasetEntity.setSchemaName(context.getSchema());
        datasetService.save(datasetEntity);

        return datasetEntity;
    }


}
