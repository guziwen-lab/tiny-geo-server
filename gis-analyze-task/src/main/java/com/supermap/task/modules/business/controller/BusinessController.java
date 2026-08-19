package com.supermap.task.modules.business.controller;

import com.supermap.core.pojo.R;
import com.supermap.task.modules.business.dto.QtnydbhAnalyzeDTO;
import com.supermap.task.modules.business.service.OtherAgriculturalLandService;
import com.supermap.task.modules.compose.entity.ComposeEntity;
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
        ComposeEntity composeEntity = otherAgriculturalLandService.analyze(dto);
        return R.ok(composeEntity.getId());
    }

}
