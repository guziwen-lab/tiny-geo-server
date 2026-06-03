package com.supermap.modules.sys.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeWithGeomDTO;
import com.supermap.modules.sys.dto.AdministrativeDivisionCodeWithGeomSaveDTO;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeWithGeomEntity;
import com.supermap.modules.sys.service.AdministrativeDivisionCodeWithGeomService;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
@Tag(name = "行政区划代码表")
@RestController
@RequestMapping("/sys/administrativedivisioncodewithgeom")
public class AdministrativeDivisionCodeWithGeomController {

    private final AdministrativeDivisionCodeWithGeomService administrativeDivisionCodeWithGeomService;

    public AdministrativeDivisionCodeWithGeomController(AdministrativeDivisionCodeWithGeomService administrativeDivisionCodeWithGeomService) {
        this.administrativeDivisionCodeWithGeomService = administrativeDivisionCodeWithGeomService;
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<AdministrativeDivisionCodeWithGeomEntity>> page(@RequestBody AdministrativeDivisionCodeWithGeomDTO dto) {
        Page<AdministrativeDivisionCodeWithGeomEntity> page = administrativeDivisionCodeWithGeomService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<AdministrativeDivisionCodeWithGeomEntity> info(@PathVariable("id") Long id) {
        AdministrativeDivisionCodeWithGeomEntity administrativeDivisionCodeWithGeom = administrativeDivisionCodeWithGeomService.getById(id);
        return R.ok(administrativeDivisionCodeWithGeom);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) AdministrativeDivisionCodeWithGeomSaveDTO dto) {
        Long id = administrativeDivisionCodeWithGeomService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) AdministrativeDivisionCodeWithGeomSaveDTO dto) {
        administrativeDivisionCodeWithGeomService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        administrativeDivisionCodeWithGeomService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
