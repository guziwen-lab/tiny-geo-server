package com.supermap.modules.sys.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.entity.DictItemEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.DictDTO;
import com.supermap.modules.sys.dto.DictSaveDTO;
import com.supermap.modules.sys.entity.DictEntity;
import com.supermap.modules.sys.service.DictService;

/**
 * 字典表
 *
 * @author gzw
 */
@Tag(name = "字典表")
@RestController
@RequestMapping("/sys/dict")
@AllArgsConstructor
public class DictController {

    private final DictService dictService;

    @Operation(summary = "字典项树形列表")
    @PostMapping("/tree")
    public R<List<DictItemEntity>> tree(@RequestBody @Validated DictDTO dto) {
        List<DictItemEntity> page = dictService.tree(dto);
        return R.ok(page);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<DictEntity>> page(@RequestBody SearchDTO dto) {
        Page<DictEntity> page = dictService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{dictId}")
    public R<DictEntity> info(@PathVariable("dictId") Long dictId) {
        DictEntity dict = dictService.getById(dictId);
        return R.ok(dict);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) DictSaveDTO dto) {
        Long dictId = dictService.saveDTO(dto);
        return R.ok(dictId);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) DictSaveDTO dto) {
        dictService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] dictIds) {
        dictService.removeByIds(Arrays.asList(dictIds));
        return R.ok();
    }

}
