package com.supermap.modules.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.modules.log.dao.AccessDao;
import com.supermap.modules.log.dto.AccessDTO;
import com.supermap.modules.log.entity.AccessEntity;
import com.supermap.modules.log.service.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service("accessService")
public class AccessServiceImpl extends ServiceImpl<AccessDao, AccessEntity> implements AccessService {

    @Override
    public Page<AccessEntity> queryPage(AccessDTO dto) {
        LambdaQueryWrapper<AccessEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Async("logExecutor")
    @Override
    public void asyncSave(AccessEntity entity) {
        try {
            save(entity);
        } catch (Exception e) {
            log.error("保存访问日志失败", e);
        }
    }

}