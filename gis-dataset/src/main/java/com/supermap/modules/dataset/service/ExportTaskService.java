package com.supermap.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.dataset.entity.ExportTaskEntity;
import com.supermap.modules.dataset.dto.ExportTaskDTO;
import com.supermap.modules.dataset.dto.ExportTaskSaveDTO;

/**
 * 导出geo数据
 *
 * @author gzw
 */
public interface ExportTaskService extends IService<ExportTaskEntity> {

    Page<ExportTaskEntity> queryPage(ExportTaskDTO dto);

    Long saveDTO(ExportTaskSaveDTO dto);

    void updateDTOById(ExportTaskSaveDTO dto);

    Long exportShp(String tableName);

    Long exportGdb(String tableName);

}

