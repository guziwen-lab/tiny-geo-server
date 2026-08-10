package com.supermap.platform.modules.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.platform.modules.platform.dto.BboxQueryDTO;
import com.supermap.platform.modules.platform.dto.FeatureDTO;
import com.supermap.platform.modules.platform.vo.FeatureVO;

/**
 * geo feature
 *
 * @author gzw
 */
public interface FeatureService {

    Page<FeatureVO> queryPage(FeatureDTO dto);

    String bboxQuery(BboxQueryDTO dto);

    FeatureVO getVOById(Long id);

}

