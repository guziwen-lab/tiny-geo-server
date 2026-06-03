package com.supermap.modules.platform.service.impl;

import com.supermap.common.util.JSON;
import com.supermap.modules.platform.dao.FeatureDao;
import com.supermap.modules.platform.dto.UploadGeoJsonDTO;
import com.supermap.modules.platform.dto.UploadWktDTO;
import com.supermap.modules.platform.entity.FeatureEntity;
import com.supermap.modules.platform.service.FeatureService;
import com.supermap.modules.platform.service.UploadService;
import com.supermap.util.IdentifierGeneratorUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final FeatureService featureService;

    private final FeatureDao featureDao;

    @Override
    public void uploadGeoJson(UploadGeoJsonDTO dto) {
        FeatureEntity featureEntity = new FeatureEntity();
        featureEntity.setId(IdentifierGeneratorUtils.nextId());
        featureEntity.setName(dto.getName());
        featureDao.saveWithGeoJson(featureEntity, dto.getGeoJson().toString());
    }

    @Override
    public void uploadWkt(UploadWktDTO dto) {
        String wkt = dto.getWkt();
        Integer srid = dto.getSrid();

        FeatureEntity featureEntity = new FeatureEntity();
        featureEntity.setId(IdentifierGeneratorUtils.nextId());
        featureEntity.setName(dto.getName());
        featureEntity.setProperties(JSON.toJSONString(dto.getProperties()));
        featureDao.saveWithWkt(featureEntity, wkt, srid);
    }

}
