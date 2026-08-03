package com.supermap.modules.business.controller;

import com.supermap.common.pojo.R;
import com.supermap.modules.business.dto.KfqChangeStatusDTO;
import com.supermap.modules.business.dto.KfqConstructionDensityDTO;
import com.supermap.modules.business.dto.KfqConstructionStatusDTO;
import com.supermap.modules.business.dto.KfqElementNameDTO;
import com.supermap.modules.business.dto.KfqLandUseDTO;
import com.supermap.modules.business.dto.KfqPreprocessDTO;
import com.supermap.modules.business.dto.DatasetQualityCheckDTO;
import com.supermap.modules.business.dto.QtnydbhAnalyzeDTO;
import com.supermap.modules.business.service.DevelopmentZonePreprocessResult;
import com.supermap.modules.business.service.OverlayQualityCheckService;
import com.supermap.modules.business.vo.DatasetQualityReportVO;
import com.supermap.modules.business.service.DevelopmentZoneService;
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
    private final DevelopmentZoneService developmentZoneService;
    private final OverlayQualityCheckService overlayQualityCheckService;

    @Operation(summary = "叠加结果质量校验（比例范围、面积守恒）")
    @PostMapping("/quality-check")
    public R<DatasetQualityReportVO> qualityCheck(@RequestBody @Validated DatasetQualityCheckDTO dto) {
        return R.ok(overlayQualityCheckService.check(dto.getDatasetId(), dto.getRatioFields(), dto.getSourceIdField(),
                dto.getOriginalField(), dto.getSplitField(), dto.getCountyField(), dto.getTolerance()));
    }

    // ======================== 其他农用地分析 ========================

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

    // ======================== 开发区分析 ========================

    @Operation(summary = "开发区行政区预处理（KFQ/JD 与县级行政区叠加并补全省市属性）")
    @PostMapping("/kfq/preprocess")
    public R<DevelopmentZonePreprocessResult> kfqPreprocess(@RequestBody @Validated KfqPreprocessDTO dto) {
        return R.ok(developmentZoneService.preprocess(dto.getKfqDatasetId(), dto.getJdDatasetId(),
                dto.getXzqDatasetId(), dto.getProvinceCodeNameJsonPath(), dto.getCityCodeNameJsonPath()));
    }

    @Operation(summary = "开发区建设状态输出")
    @PostMapping("/kfq/construction-status")
    public R<Long> kfqConstructionStatus(@RequestBody @Validated KfqConstructionStatusDTO dto) {
        Long datasetId = developmentZoneService.exportConstructionStatus(dto.getKfqDatasetId());
        return R.ok(datasetId);
    }

    @Operation(summary = "开发区建设密度分析")
    @PostMapping("/kfq/construction-density")
    public R<Long> kfqConstructionDensity(@RequestBody @Validated KfqConstructionDensityDTO dto) {
        Long datasetId = developmentZoneService.analyzeConstructionDensity(
                dto.getJdDatasetId(),
                dto.getDltbDatasetId()
        );
        return R.ok(datasetId);
    }

    @Operation(summary = "开发区要素名称分析")
    @PostMapping("/kfq/element-name")
    public R<Long> kfqElementName(@RequestBody @Validated KfqElementNameDTO dto) {
        Long datasetId = developmentZoneService.analyzeElementName(
                dto.getKfqDatasetId(),
                dto.getJdDatasetId()
        );
        return R.ok(datasetId);
    }

    @Operation(summary = "开发区变化情况分析")
    @PostMapping("/kfq/change-status")
    public R<Long> kfqChangeStatus(@RequestBody @Validated KfqChangeStatusDTO dto) {
        Long datasetId = developmentZoneService.analyzeChangeStatus(dto.getKfqDatasetId());
        return R.ok(datasetId);
    }

    @Operation(summary = "开发区土地利用现状分析（非同口径/同口径）")
    @PostMapping("/kfq/land-use")
    public R<Long> kfqLandUse(@RequestBody @Validated KfqLandUseDTO dto) {
        Long datasetId = developmentZoneService.analyzeLandUse(
                dto.getKfqDatasetId(),
                dto.getDltbDatasetId(),
                dto.getCaliber()
        );
        return R.ok(datasetId);
    }

}
