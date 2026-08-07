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
    public <T extends AnalysisParam> void markSuccess(Long taskId, AnalysisResult result, AnalysisContext<T> context) {
        // 保存步骤
        saveSteps(taskId, context.getSteps());

        // 结果记录到数据集
        DatasetEntity resultDatasetEntity = saveResultToDataset(result, context.getSchema());

        Instant now = Instant.now();
        TaskEntity taskEntity = taskDao.selectById(taskId);
        taskEntity.setGeomType(context.getGeomType());
        taskEntity.setStatus(TaskStatus.SUCCESS);
        taskEntity.setCost(now.getEpochSecond() - taskEntity.getStartedAt().getEpochSecond());
        taskEntity.setFinishedAt(now);
        taskEntity.setResultDatasetId(resultDatasetEntity.getId());
        taskDao.updateById(taskEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markFailed(Long taskId, String message) {
        Instant now = Instant.now();

        TaskEntity taskEntity = taskDao.selectById(taskId);
        taskEntity.setStatus(TaskStatus.FAILED);
        taskEntity.setFinishedAt(now);
        taskEntity.setCost(now.getEpochSecond() - taskEntity.getStartedAt().getEpochSecond());
        taskEntity.setMessage(message);
        taskDao.updateById(taskEntity);
    }

    private void saveSteps(Long taskId, List<AnalysisStep> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }

        List<TaskStepEntity> stepEntities = new ArrayList<>(steps.size());
        for (AnalysisStep step : steps) {
            // 保存步骤
            TaskStepEntity stepEntity = new TaskStepEntity();
            stepEntity.setTaskId(taskId);
            stepEntity.setStepNo(step.getStepNo());
            stepEntity.setInputTable(step.getInputTable());
            stepEntity.setOverlayTable(step.getOverlayTable());
            stepEntity.setOutputTable(step.getOutputTable());
            stepEntities.add(stepEntity);
        }

        taskStepService.saveBatch(stepEntities);
    }

    private DatasetEntity saveResultToDataset(AnalysisResult result, String schema) {
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(result.getResultTableName());
        datasetEntity.setSchemaName(result.getSchemaName());
        datasetEntity.setTableName(result.getResultTableName());
        datasetEntity.setGeomType(result.getGeomType());
        datasetEntity.setSrid(result.getSrid());
        datasetEntity.setFeatureCount(result.getFeatureCount());
        datasetEntity.setCreatedAt(Instant.now());
        datasetEntity.setStatus(UploadStatus.SUCCESS);
        datasetEntity.setSchemaName(schema);
        datasetService.save(datasetEntity);

        return datasetEntity;
    }


}
