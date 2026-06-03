package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.AdministrativeDivisionCodeWithGeomDao;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeWithGeomEntity;
import com.supermap.modules.sys.service.AdministrativeDivisionCodeWithGeomService;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeWithGeomDTO;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeWithGeomSaveDTO;

@Service("administrativeDivisionCodeWithGeomService")
public class AdministrativeDivisionCodeWithGeomServiceImpl extends ServiceImpl<AdministrativeDivisionCodeWithGeomDao, AdministrativeDivisionCodeWithGeomEntity> implements AdministrativeDivisionCodeWithGeomService {

    @Override
    public Page<AdministrativeDivisionCodeWithGeomEntity> queryPage(AdministrativeDivisionCodeWithGeomDTO dto) {
        LambdaQueryWrapper<AdministrativeDivisionCodeWithGeomEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(AdministrativeDivisionCodeWithGeomSaveDTO dto) {
        AdministrativeDivisionCodeWithGeomEntity administrativeDivisionCodeWithGeomEntity = new AdministrativeDivisionCodeWithGeomEntity();
        BeanUtils.copyProperties(dto, administrativeDivisionCodeWithGeomEntity);
        save(administrativeDivisionCodeWithGeomEntity);
        return administrativeDivisionCodeWithGeomEntity.getId();
    }

    @Override
    public void updateDTOById(AdministrativeDivisionCodeWithGeomSaveDTO dto) {
        AdministrativeDivisionCodeWithGeomEntity administrativeDivisionCodeWithGeomEntity = new AdministrativeDivisionCodeWithGeomEntity();
        BeanUtils.copyProperties(dto, administrativeDivisionCodeWithGeomEntity);
        updateById(administrativeDivisionCodeWithGeomEntity);
    }

}