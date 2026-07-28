package com.supermap.modules.dataset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.enums.UploadStatus;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.dataset.dao.DatasetDao;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.modules.dataset.dto.DatasetDTO;
import com.supermap.modules.dataset.dto.DatasetSaveDTO;

@Service("datasetService")
public class DatasetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements DatasetService {

    @Override
    public Page<DatasetEntity> queryPage(DatasetDTO dto) {
        LambdaQueryWrapper<DatasetEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(DatasetSaveDTO dto) {
        DatasetEntity datasetEntity = new DatasetEntity();
        BeanUtils.copyProperties(dto, datasetEntity);
        save(datasetEntity);
        return datasetEntity.getId();
    }

    @Override
    public void updateDTOById(DatasetSaveDTO dto) {
        DatasetEntity datasetEntity = new DatasetEntity();
        BeanUtils.copyProperties(dto, datasetEntity);
        updateById(datasetEntity);
    }

    @Override
    public boolean updateStatusBySuccess(Long id, UploadStatus uploadStatus) {
        return baseMapper.updateStatusBySuccess(id, uploadStatus) > 0;
    }

}