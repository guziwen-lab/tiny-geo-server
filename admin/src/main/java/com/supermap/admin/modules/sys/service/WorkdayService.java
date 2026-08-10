package com.supermap.admin.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.admin.modules.sys.entity.WorkdayEntity;
import com.supermap.admin.modules.sys.dto.WorkdayDTO;
import com.supermap.admin.modules.sys.dto.WorkdaySaveDTO;

/**
 * 工作日表
 *
 * @author gzw
 */
public interface WorkdayService extends IService<WorkdayEntity> {

    Page<WorkdayEntity> queryPage(WorkdayDTO dto);

    Long saveDTO(WorkdaySaveDTO dto);

    void updateDTOById(WorkdaySaveDTO dto);

}

