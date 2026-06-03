package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.StringUtils;
import com.supermap.modules.sys.entity.DictItemEntity;
import com.supermap.modules.sys.service.DictItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.DictDao;
import com.supermap.modules.sys.entity.DictEntity;
import com.supermap.modules.sys.service.DictService;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.dto.DictDTO;
import com.supermap.modules.sys.dto.DictSaveDTO;

import java.sql.Timestamp;
import java.util.List;

@Service("dictService")
public class DictServiceImpl extends ServiceImpl<DictDao, DictEntity> implements DictService {

    @Resource
    private DictItemService dictItemService;

    @Override
    public Page<DictEntity> queryPage(SearchDTO dto) {
        /*LambdaQueryWrapper<DictEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(dto.getStartTime() != null, DictEntity::getCreateTime, dto.getStartTime());
        wrapper.le(dto.getEndTime() != null, DictEntity::getCreateTime, dto.getEndTime());
        wrapper.like(StringUtils.isNotBlank(dto.getName()), DictEntity::getName, dto.getName());
        wrapper.orderByDesc(DictEntity::getCreateTime);
        return page(dto.page(), wrapper);*/

        return baseMapper.page(dto.page(), dto);
    }

    @Override
    public Long saveDTO(DictSaveDTO dto) {
        checkName(dto.getName());

        DictEntity dictEntity = new DictEntity();
        BeanUtils.copyProperties(dto, dictEntity);
        dictEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        dictEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        save(dictEntity);
        return dictEntity.getDictId();
    }

    @Override
    public void updateDTOById(DictSaveDTO dto) {
        DictEntity dictEntity = new DictEntity();
        BeanUtils.copyProperties(dto, dictEntity);
        dictEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        if (StringUtils.isEmpty(dto.getName())) {
            updateById(dictEntity);
            return;
        }

        DictEntity byId = getById(dto.getDictId());
        if (byId.getName().equals(dto.getName())) {
            updateById(dictEntity);
            return;
        }

        checkName(dto.getName());
        updateById(dictEntity);
    }

    @Override
    public DictEntity getByName(String name) {
        return getOne(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getName, name));
    }

    @Override
    public List<DictItemEntity> tree(DictDTO dto) {
        if (dto.getDictId() != null) {
            DictEntity dictEntity = getById(dto.getDictId());
            if (dictEntity == null)
                throw new RuntimeException("字典不存在");
            return dictItemService.tree(dto.getDictId());
        } else if (StringUtils.isNotEmpty(dto.getName())) {
            DictEntity dictEntity = getByName(dto.getName());
            if (dictEntity == null)
                throw new RuntimeException("字典不存在");
            return dictItemService.tree(dictEntity.getDictId());
        } else {
            throw new RuntimeException("字典不存在");
        }
    }

    private void checkName(String name) {
        long count = count(new LambdaQueryWrapper<DictEntity>()
                .eq(DictEntity::getName, name));
        if (count > 0)
            throw new RuntimeException("字典名称已存在");
    }

}