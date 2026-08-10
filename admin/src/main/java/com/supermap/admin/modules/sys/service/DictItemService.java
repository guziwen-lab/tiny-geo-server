package com.supermap.admin.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.admin.modules.sys.entity.DictItemEntity;
import com.supermap.admin.modules.sys.dto.DictItemDTO;
import com.supermap.admin.modules.sys.dto.DictItemSaveDTO;

import java.util.List;

/**
 * 字典项表
 *
 * @author gzw
 */
public interface DictItemService extends IService<DictItemEntity> {

    Page<DictItemEntity> queryPage(DictItemDTO dto);

    Long saveDTO(DictItemSaveDTO dto);

    void updateDTOById(DictItemSaveDTO dto);

    List<DictItemEntity> tree(Long dictId);

    List<DictItemEntity> getByDictName(String dictName);

    DictItemEntity getByCodeAndDictName(String code, String dictName);

}

