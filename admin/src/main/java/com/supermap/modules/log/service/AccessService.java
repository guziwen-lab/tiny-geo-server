package com.supermap.modules.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.modules.log.dto.AccessDTO;
import com.supermap.modules.log.entity.AccessEntity;

/**
 * 全局日志表
 *
 * @author gzw
 */
public interface AccessService extends IService<AccessEntity> {

    Page<AccessEntity> queryPage(AccessDTO dto);

    void asyncSave(AccessEntity entity);

}

