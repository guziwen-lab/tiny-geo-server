package com.supermap.modules.analyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.analyze.dao.DatasetDao;
import com.supermap.modules.analyze.entity.DatasetEntity;
import com.supermap.modules.analyze.service.DatasetService;
import com.supermap.modules.analyze.dto.DatasetDTO;
import com.supermap.modules.analyze.dto.DatasetSaveDTO;

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