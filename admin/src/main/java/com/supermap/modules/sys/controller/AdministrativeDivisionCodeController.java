package com.supermap.modules.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeDTO;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeEntity;
import com.supermap.modules.sys.service.AdministrativeDivisionCodeService;
import com.supermap.modules.sys.vo.ProvinceAndCityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
@Tag(name = "行政区划代码表")
@RestController
@RequestMapping("/sys/administrativedivisioncode")
public class AdministrativeDivisionCodeController {

    private final AdministrativeDivisionCodeService administrativeDivisionCodeService;

    public AdministrativeDivisionCodeController(AdministrativeDivisionCodeService administrativeDivisionCodeService) {
        this.administrativeDivisionCodeService = administrativeDivisionCodeService;
    }

    @Operation(summary = "树形结构")
    @GetMapping("/tree")
    public R<List<AdministrativeDivisionCodeEntity>> tree(@RequestParam String administrativeDivisionCode) {
        List<AdministrativeDivisionCodeEntity> tree = administrativeDivisionCodeService.tree(administrativeDivisionCode);
        return R.ok(tree);
    }

    @Operation(summary = "全部的行政区的树形结构")
    @GetMapping("/tree/all")
    public R<List<AdministrativeDivisionCodeEntity>> treeAll() {
        List<AdministrativeDivisionCodeEntity> tree = administrativeDivisionCodeService.tree();
        return R.ok(tree);
    }

    @Operation(summary = "查询下级")
    @GetMapping("/list/subordinates/{code}")
    public R<List<AdministrativeDivisionCodeEntity>> subordinates(@PathVariable("code") String code) {
        List<AdministrativeDivisionCodeEntity> entities = administrativeDivisionCodeService.getSubordinates(code);
        return R.ok(entities);
    }

    @Operation(summary = "查询上级")
    @GetMapping("/list/superior/{code}")
    public R<AdministrativeDivisionCodeEntity> superior(@PathVariable("code") String code) {
        AdministrativeDivisionCodeEntity entities = administrativeDivisionCodeService.getSuperior(code);
        return R.ok(entities);
    }

    @Operation(summary = "区县查询所属的省市")
    @GetMapping("/province/city/{code}")
    public R<ProvinceAndCityVO> getProvinceAndCityByDistrictCode(@PathVariable("code") String code) {
        ProvinceAndCityVO vo = administrativeDivisionCodeService.getProvinceAndCityByDistrictCode(code);
        return R.ok(vo);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/list")
    public R<Page<AdministrativeDivisionCodeEntity>> list(@RequestBody AdministrativeDivisionCodeDTO dto) {
        Page<AdministrativeDivisionCodeEntity> page = administrativeDivisionCodeService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "信息")
    @GetMapping("/info/{code}")
    public R<AdministrativeDivisionCodeEntity> info(@PathVariable("code") String code) {
        AdministrativeDivisionCodeEntity administrativeDivisionCode = administrativeDivisionCodeService.getById(code);
        return R.ok(administrativeDivisionCode);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Void> save(@RequestBody @Validated AdministrativeDivisionCodeEntity entity) {
        administrativeDivisionCodeService.save(entity);
        return R.ok();
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated AdministrativeDivisionCodeEntity entity) {
        administrativeDivisionCodeService.save(entity);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        administrativeDivisionCodeService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
