package com.supermap.modules.dataset.service;

import com.supermap.enums.UploadStatus;
import com.supermap.modules.dataset.dao.ExportTaskDao;
import com.supermap.modules.dataset.entity.ExportTaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ExportStatusUpdater {

    private final ExportTaskDao exportTaskDao;

    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(Long taskId) {
        ExportTaskEntity task = exportTaskDao.selectById(taskId);
        if (task != null) {
            task.setStatus(UploadStatus.SUCCESS);
            task.setFinishedAt(Instant.now());
            exportTaskDao.updateById(task);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void markFailed(Long taskId, String message) {
        ExportTaskEntity task = exportTaskDao.selectById(taskId);
        if (task != null) {
            task.setStatus(UploadStatus.FAILED);
            task.setFinishedAt(Instant.now());
            task.setMessage(message);
            exportTaskDao.updateById(task);
        }
    }
}