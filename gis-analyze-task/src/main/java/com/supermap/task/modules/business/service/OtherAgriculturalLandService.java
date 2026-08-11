package com.supermap.task.modules.business.service;

import com.supermap.task.modules.business.dto.QtnydbhAnalyzeDTO;
import com.supermap.task.modules.compose.entity.ComposeEntity;

/**
 * 其他农用地分析服务
 * <p>
 * 2025年自然资源监测-其他农用地图斑分析。
 * 将ZT监测图层与DLTB地类图斑相交并按面积拆分，
 * 然后按口径过滤提取其他农用地变化图斑。
 *
 * @author gzw
 */
public interface OtherAgriculturalLandService {

    /**
     * 执行其他农用地分析
     *
     * @return ComposeEntity id
     */
    ComposeEntity analyze(QtnydbhAnalyzeDTO dto);

}
