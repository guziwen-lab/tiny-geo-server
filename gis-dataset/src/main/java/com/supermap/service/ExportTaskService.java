package com.supermap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.entity.ExportTaskEntity;
import com.supermap.dto.ExportTaskDTO;
import com.supermap.dto.ExportTaskSaveDTO;

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

