package com.supermap.service;

import com.supermap.dao.OverlayMapper;
import com.supermap.util.DsTempSnGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 */
@Service
@RequiredArgsConstructor
public class OverlayService extends AbstractExecuteService {

    private final OverlayMapper overlayMapper;

    private final DsTempSnGenerator dsTempSnGenerator;

    @Override
    public String executeInternal(String current, String next) {
        String result = "ds_temp_" + dsTempSnGenerator.generate();
        overlayMapper.executeOverlay(current, next, result);
        return result;
    }

}
