package com.supermap.modules.platform.service.impl;

import com.supermap.modules.platform.dao.FeatureDao;
import com.supermap.modules.platform.service.GeometryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@AllArgsConstructor
public class GeometryServiceImpl implements GeometryService {

    private final FeatureDao featureDao;

}
