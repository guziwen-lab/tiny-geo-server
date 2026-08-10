package com.supermap.admin.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.db.dto.SearchDTO;
import com.supermap.admin.modules.sys.entity.DictEntity;
import com.supermap.admin.modules.sys.dto.DictDTO;
import com.supermap.admin.modules.sys.dto.DictSaveDTO;
import com.supermap.admin.modules.sys.entity.DictItemEntity;

import java.util.List;

/**
 * 字典表
 *
 * @author gzw
 */
public interface DictService extends IService<DictEntity> {

    Page<DictEntity> queryPage(SearchDTO dto);

    Long saveDTO(DictSaveDTO dto);

    void updateDTOById(DictSaveDTO dto);

    DictEntity getByName(String name);

    List<DictItemEntity> tree(DictDTO dto);

}

