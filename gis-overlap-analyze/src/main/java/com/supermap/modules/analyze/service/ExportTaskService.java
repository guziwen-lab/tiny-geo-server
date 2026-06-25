package com.supermap.modules.analyze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyze.entity.ExportTaskEntity;
import com.supermap.modules.analyze.dto.ExportTaskDTO;
import com.supermap.modules.analyze.dto.ExportTaskSaveDTO;

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

