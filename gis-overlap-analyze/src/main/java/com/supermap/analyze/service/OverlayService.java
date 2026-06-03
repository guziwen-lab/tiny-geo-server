package com.supermap.analyze.service;

import com.supermap.analyze.dao.OverlayMapper;
import com.supermap.analyze.security.SqlInjectionCheck;
import com.supermap.util.DsTempSnGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlayService {

    private final OverlayMapper overlayMapper;

    private final DsTempSnGenerator dsTempSnGenerator;

    public String executeOverlay(String current, String next) {
        SqlInjectionCheck.checkTableName(current, next);

        String result = "ds_temp_" + dsTempSnGenerator.generate();
        overlayMapper.executeOverlay(current, next, result);

        return result;
    }

}
