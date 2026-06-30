package com.supermap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.dao.DatasetDao;
import com.supermap.entity.DatasetEntity;
import com.supermap.service.DatasetService;
import com.supermap.dto.DatasetDTO;
import com.supermap.dto.DatasetSaveDTO;

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

}