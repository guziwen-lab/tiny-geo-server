package com.supermap.modules.business.controller;

import com.supermap.common.pojo.R;
import com.supermap.modules.business.dto.QtnydbhAnalyzeDTO;
import com.supermap.modules.business.service.OtherAgriculturalLandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2026第二季度自然资源监测业务分析
 * @author gzw
 */
@Tag(name = "2026第二季度自然资源监测业务分析")
@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final OtherAgriculturalLandService otherAgriculturalLandService;

    /**
     * 其他农用地分析
     * jctb(zt)叠加dltb
     *
     * @return 结果dataset id
     */
    @Operation(summary = "其他农用地分析（非同口径/同口径）")
    @PostMapping("/qtnydbh/analyze")
    public R<Long> qtnydbhAnalyze(@RequestBody @Validated QtnydbhAnalyzeDTO dto) {
        Long datasetId = otherAgriculturalLandService.analyze(
                dto.getZtDatasetId(),
                dto.getDltbDatasetId(),
                dto.getCaliber()
        );
        return R.ok(datasetId);
    }

}
