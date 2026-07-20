package com.supermap.modules.platform.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.platform.dto.BboxQueryDTO;
import com.supermap.modules.platform.dto.FeatureDTO;
import com.supermap.modules.platform.vo.FeatureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author gzw
 */
@Mapper
public interface SearchFeatureDao {

    String queryNormalBbox(@Param("dto") BboxQueryDTO dto);

    String queryCrossDatelineBbox(@Param("dto") BboxQueryDTO dto);

    Page<FeatureVO> queryPage(Page<Object> page, @Param("dto") FeatureDTO dto);

    FeatureVO getVOById(Long id);

}
