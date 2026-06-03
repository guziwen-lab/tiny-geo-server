package com.supermap.modules.platform.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.platform.dto.BboxQueryDTO;
import com.supermap.modules.platform.vo.FeatureVO;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.platform.dao.FeatureDao;
import com.supermap.modules.platform.entity.FeatureEntity;
import com.supermap.modules.platform.service.FeatureService;
import com.supermap.modules.platform.dto.FeatureDTO;

import java.util.List;

@Service("featureService")
public class FeatureServiceImpl extends ServiceImpl<FeatureDao, FeatureEntity> implements FeatureService {

    @Override
    public Page<FeatureVO> queryPage(FeatureDTO dto) {
        return baseMapper.queryPage(dto.page(), dto);
    }

    @Override
    public String bboxQuery(BboxQueryDTO dto) {
        Double minX = dto.getMinX();
        Double maxX = dto.getMaxX();
        boolean crossDateline = minX > maxX;

        if (!crossDateline) {
            return baseMapper.queryNormalBbox(dto);
        } else {
            return baseMapper.queryCrossDatelineBbox(dto);
        }
    }

    @Override
    public FeatureVO getVOById(Long id) {
        return baseMapper.getVOById(id);
    }

}