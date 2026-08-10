package com.supermap.dataset.modules.dataset.service;

import com.supermap.gis.enums.GeomType;
import com.supermap.dataset.enums.UploadStatus;
import com.supermap.dataset.modules.dataset.dao.DatasetDao;
import com.supermap.dataset.modules.dataset.entity.DatasetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ImportStatusUpdater {

    private final DatasetDao datasetDao;

    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(Long datasetId, GeomType geomType, Integer srid, Long featureCount) {
        DatasetEntity entity = datasetDao.selectById(datasetId);
        entity.setStatus(UploadStatus.SUCCESS);
        entity.setGeomType(geomType);
        entity.setSrid(srid);
        entity.setFeatureCount(featureCount);
        datasetDao.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markFailed(Long datasetId, String message) {
        DatasetEntity entity = datasetDao.selectById(datasetId);
        entity.setStatus(UploadStatus.FAILED);
        entity.setMessage(message);
        datasetDao.updateById(entity);
    }

}
