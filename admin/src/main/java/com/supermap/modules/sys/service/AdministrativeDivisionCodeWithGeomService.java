package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeWithGeomEntity;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeWithGeomDTO;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeWithGeomSaveDTO;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
public interface AdministrativeDivisionCodeWithGeomService extends IService<AdministrativeDivisionCodeWithGeomEntity> {

    Page<AdministrativeDivisionCodeWithGeomEntity> queryPage(AdministrativeDivisionCodeWithGeomDTO dto);

    Long saveDTO(AdministrativeDivisionCodeWithGeomSaveDTO dto);

    void updateDTOById(AdministrativeDivisionCodeWithGeomSaveDTO dto);

}

