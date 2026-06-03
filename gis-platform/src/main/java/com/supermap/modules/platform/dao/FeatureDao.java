package com.supermap.modules.platform.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.platform.dto.BboxQueryDTO;
import com.supermap.modules.platform.dto.FeatureDTO;
import com.supermap.modules.platform.entity.FeatureEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.platform.vo.FeatureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * geo feature
 *
 * @author gzw
 */
@Mapper
public interface FeatureDao extends BaseMapper<FeatureEntity> {

    void saveWithGeoJson(@Param("featureEntity") FeatureEntity featureEntity, @Param("geoJson") String geoJson);

    void saveWithWkt(@Param("featureEntity") FeatureEntity featureEntity, @Param("wkt") String wkt, @Param("srid") Integer srid);

    String queryNormalBbox(@Param("dto") BboxQueryDTO dto);

    String queryCrossDatelineBbox(@Param("dto") BboxQueryDTO dto);

    Page<FeatureVO> queryPage(Page<Object> page, @Param("dto") FeatureDTO dto);

    FeatureVO getVOById(Long id);

}
