package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.modules.sys.entity.DictEntity;
import com.supermap.modules.sys.service.DictService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.DictItemDao;
import com.supermap.modules.sys.entity.DictItemEntity;
import com.supermap.modules.sys.service.DictItemService;
import com.supermap.modules.sys.dto.DictItemDTO;
import com.supermap.modules.sys.dto.DictItemSaveDTO;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service("dictItemService")
public class DictItemServiceImpl extends ServiceImpl<DictItemDao, DictItemEntity> implements DictItemService {

    @Resource
    @Lazy
    private DictService dictService;

    @Override
    public Page<DictItemEntity> queryPage(DictItemDTO dto) {
        LambdaQueryWrapper<DictItemEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(DictItemSaveDTO dto) {
        Long dictId = dto.getDictId();
        DictEntity dictEntity = dictService.getById(dictId);
        if (dictEntity == null)
            throw new RuntimeException("字典不存在");

        DictItemEntity dictItemEntity = new DictItemEntity();
        BeanUtils.copyProperties(dto, dictItemEntity);
        if (dictItemEntity.getParentId() == null)
            dictItemEntity.setParentId(0L);
        dictItemEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        dictItemEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        try {
            save(dictItemEntity);
        } catch (DataIntegrityViolationException ex) {
            log.debug("字典项编码或字典项名称已存在", ex);
            throw new DataIntegrityViolationException("字典项编码或字典项名称已存在");
        }
        return dictItemEntity.getDictItemId();
    }

    @Override
    public void updateDTOById(DictItemSaveDTO dto) {
        Long dictId = dto.getDictId();
        DictEntity dictEntity = dictService.getById(dictId);
        if (dictEntity == null)
            throw new RuntimeException("字典不存在");

        DictItemEntity dictItemEntity = new DictItemEntity();
        BeanUtils.copyProperties(dto, dictItemEntity);
        dictItemEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        updateById(dictItemEntity);
    }

    @Override
    public List<DictItemEntity> tree(Long dictId) {
        List<DictItemEntity> list = list(new LambdaQueryWrapper<>(DictItemEntity.class)
                .eq(DictItemEntity::getDictId, dictId)
                .orderByAsc(DictItemEntity::getSort)
                .orderByAsc(DictItemEntity::getCreateTime));

        List<DictItemEntity> collect = list.stream()
                .filter(dict -> dict.getParentId() == null || dict.getParentId().equals(0L))
                .toList();
        collect.forEach(dict -> setChildren(dict, list));

        return collect;
    }

    @Override
    public List<DictItemEntity> getByDictName(String dictName) {
        return baseMapper.getByDictName(dictName);
    }

    @Override
    public DictItemEntity getByCodeAndDictName(String code, String dictName) {
        return baseMapper.getByCodeAndDictName(code, dictName);
    }

    private void setChildren(DictItemEntity dict, List<DictItemEntity> list) {
        List<DictItemEntity> children = list.stream()
                .filter(d -> dict.getDictItemId().equals(d.getParentId()))
                .toList();
        dict.getChildren().addAll(children);
        children.forEach(d -> setChildren(d, list));
    }

}