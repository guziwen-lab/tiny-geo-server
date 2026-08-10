package com.supermap.dataset.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.dataset.enums.UploadStatus;
import com.supermap.dataset.modules.dataset.entity.DatasetEntity;
import com.supermap.dataset.modules.dataset.dto.DatasetDTO;
import com.supermap.dataset.modules.dataset.dto.DatasetSaveDTO;

/**
 * 数据集表
 *
 * @author gzw
 */
public interface DatasetService extends IService<DatasetEntity> {

    Page<DatasetEntity> queryPage(DatasetDTO dto);

    Long saveDTO(DatasetSaveDTO dto);

    void updateDTOById(DatasetSaveDTO dto);

    boolean updateStatusBySuccess(Long id, UploadStatus uploadStatus);

}

