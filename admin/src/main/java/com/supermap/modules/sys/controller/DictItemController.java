package com.supermap.modules.sys.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.DictItemDTO;
import com.supermap.modules.sys.dto.DictItemSaveDTO;
import com.supermap.modules.sys.entity.DictItemEntity;
import com.supermap.modules.sys.service.DictItemService;

/**
 * 字典项表
 *
 * @author gzw
 */
@Tag(name = "字典项表")
@RestController
@RequestMapping("/sys/dictitem")
@AllArgsConstructor
public class DictItemController {

    private final DictItemService dictItemService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<DictItemEntity>> page(@RequestBody DictItemDTO dto) {
        Page<DictItemEntity> page = dictItemService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{dictItemId}")
    public R<DictItemEntity> info(@PathVariable("dictItemId") Long dictItemId) {
        DictItemEntity dictItem = dictItemService.getById(dictItemId);
        return R.ok(dictItem);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) DictItemSaveDTO dto) {
        Long dictItemId = dictItemService.saveDTO(dto);
        return R.ok(dictItemId);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) DictItemSaveDTO dto) {
        dictItemService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] dictItemIds) {
        dictItemService.removeByIds(Arrays.asList(dictItemIds));
        return R.ok();
    }

}
