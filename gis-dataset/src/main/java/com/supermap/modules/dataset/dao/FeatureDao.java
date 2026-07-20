package com.supermap.modules.dataset.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.dataset.entity.FeatureEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * geo feature
 *
 * @author gzw
 */
@Mapper
public interface FeatureDao extends BaseMapper<FeatureEntity> {

    void saveWithGeoJson(@Param("featureEntity") FeatureEntity featureEntity, @Param("geoJson") String geoJson);

    void saveWithWkt(@Param("featureEntity") FeatureEntity featureEntity, @Param("wkt") String wkt, @Param("srid") Integer srid);

}
