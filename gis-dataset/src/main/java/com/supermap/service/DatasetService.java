package com.supermap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.entity.DatasetEntity;
import com.supermap.dto.DatasetDTO;
import com.supermap.dto.DatasetSaveDTO;

/**
 * 数据集表
 *
 * @author gzw
 */
public interface DatasetService extends IService<DatasetEntity> {

    Page<DatasetEntity> queryPage(DatasetDTO dto);

    Long saveDTO(DatasetSaveDTO dto);

    void updateDTOById(DatasetSaveDTO dto);

}

