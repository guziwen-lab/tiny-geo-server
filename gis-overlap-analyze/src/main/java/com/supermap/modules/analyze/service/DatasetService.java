package com.supermap.modules.analyze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyze.entity.DatasetEntity;
import com.supermap.modules.analyze.dto.DatasetDTO;
import com.supermap.modules.analyze.dto.DatasetSaveDTO;

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

