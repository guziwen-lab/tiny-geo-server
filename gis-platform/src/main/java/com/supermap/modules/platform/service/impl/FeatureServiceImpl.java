package com.supermap.modules.platform.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.platform.dao.SearchFeatureDao;
import com.supermap.modules.platform.dto.BboxQueryDTO;
import com.supermap.modules.platform.vo.FeatureVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.supermap.modules.platform.service.FeatureService;
import com.supermap.modules.platform.dto.FeatureDTO;

@Service("featureService")
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {
    
    private final SearchFeatureDao searchFeatureDao;

    @Override
    public Page<FeatureVO> queryPage(FeatureDTO dto) {
        return searchFeatureDao.queryPage(dto.page(), dto);
    }

    @Override
    public String bboxQuery(BboxQueryDTO dto) {
        Double minX = dto.getMinX();
        Double maxX = dto.getMaxX();
        boolean crossDateline = minX > maxX;

        if (!crossDateline) {
            return searchFeatureDao.queryNormalBbox(dto);
        } else {
            return searchFeatureDao.queryCrossDatelineBbox(dto);
        }
    }

    @Override
    public FeatureVO getVOById(Long id) {
        return searchFeatureDao.getVOById(id);
    }

}