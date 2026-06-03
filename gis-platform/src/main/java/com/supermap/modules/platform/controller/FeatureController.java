package com.supermap.modules.platform.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.platform.dto.BboxQueryDTO;
import com.supermap.modules.platform.vo.FeatureVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.pojo.R;
import com.supermap.modules.platform.dto.FeatureDTO;
import com.supermap.modules.platform.entity.FeatureEntity;
import com.supermap.modules.platform.service.FeatureService;

/**
 * geo feature
 *
 * @author gzw
 */
@Tag(name = "geo feature")
@RestController
@RequestMapping("/platform/feature")
@AllArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @Operation(summary = "bbox查询")
    @PostMapping("/bbox")
    public R<String> bboxQuery(@RequestBody @Validated BboxQueryDTO dto) {
        String result = featureService.bboxQuery(dto);
        return R.ok(result);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<FeatureVO>> page(@RequestBody FeatureDTO dto) {
        Page<FeatureVO> page = featureService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<FeatureVO> info(@PathVariable Long id) {
        FeatureVO feature = featureService.getVOById(id);
        return R.ok(feature);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        featureService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
